/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.CrudEventSearch;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    EventRepositoryImpl repository;

    private final Instant now = Instant.now();

    @Test
    void testUpdate() {
        JobEvent input = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "00013-gtfs.zip", 2L, "1", TimeTableAction.IMPORT.toString(), JobState.OK, "1234567", now, "ost");
        Assertions.assertDoesNotThrow(() -> {
            repository.save(input);
            repository.flush();
        });
    }

    @Test
    void testSaveInvalidEvent() {
        JobEvent input = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "X".repeat(500), 2L, "1", TimeTableAction.IMPORT.toString(), JobState.OK, "1234567", now, "ost");
        repository.save(input);
        Assertions.assertThrows(ConstraintViolationException.class, () -> repository.flush());
    }

    @Test
    void testFindJobEventsForProvider() {
        JobEvent s1 = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).providerId(2L).referential("ost").state(JobState.OK).name("file1.zip").externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(now).build();
        repository.save(s1);
        JobEvent s2 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 2L, "2", TimeTableAction.EXPORT.toString(), JobState.FAILED, "corr-id-1", now.plus(1, ChronoUnit.MINUTES), "ost");
        repository.save(s2);
        JobEvent s3 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file2.zip", 3L, "1", TimeTableAction.IMPORT.toString(), JobState.TIMEOUT, "corr-id-2", now, "ost");
        repository.save(s3);
        Collection<JobEvent> eventsForProvider2 = repository.findTimetableJobEvents(Collections.singletonList(2L), null, null, null, null, null, null);
        assertThat(eventsForProvider2).hasSize(2);

        Collection<JobEvent> allEvents = repository.findTimetableJobEvents(null, null, null, null, null, null, null);
        assertThat(allEvents).hasSize(3);
    }


    @Test()
    void testGetStatusWithAllCriteria() {
        JobEvent s1 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 3L, "1", TimeTableAction.IMPORT.toString(), JobState.OK, "corr-id-1", now, "ost");
        repository.save(s1);
        JobEvent s2 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 3L, "2", TimeTableAction.EXPORT.toString(), JobState.FAILED, "corr-id-1", now.plus(1, ChronoUnit.MINUTES), "ost");
        repository.save(s2);
        JobEvent s3 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file2.zip", 3L, "1", TimeTableAction.IMPORT.toString(), JobState.TIMEOUT, "corr-id-2", now, "ost");
        repository.save(s3);

        Collection<JobEvent> statusesQueryMatchingS1 = repository.findTimetableJobEvents(Collections.singletonList(3L), now, now, Collections.singletonList(TimeTableAction.IMPORT.toString()), Collections.singletonList(JobState.OK), Collections.singletonList("1"), Collections.singletonList("file1.zip"));
        assertThat(statusesQueryMatchingS1)
                .hasSize(2)
                .contains(s1, s2);

        Collection<JobEvent> statusesQueryMatchingS1andS3 = repository.findTimetableJobEvents(Collections.singletonList(3L), now, now, Arrays.asList(TimeTableAction.IMPORT.toString(), TimeTableAction.EXPORT.toString()),
                Arrays.asList(JobState.OK, JobState.TIMEOUT), null, Arrays.asList("file1.zip", "file2.zip"));
        assertThat(statusesQueryMatchingS1andS3).hasSize(3);
    }

    @Test
    void getLatestDeliveryStatusForProvider() {
        JobEvent s1 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.OK, "corr-id-1", now, "ost");
        repository.save(s1);
        JobEvent s2 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 3L, "2", TimeTableAction.EXPORT.toString(), JobState.FAILED, "corr-id-1", now.plus(1, ChronoUnit.MINUTES), "ost");
        repository.save(s2);
        JobEvent s3 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file2.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.TIMEOUT, "corr-id-2", now.minus(1, ChronoUnit.MINUTES), "ost");
        repository.save(s3);
        JobEvent sReimport = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "reimport-file1.zip", 3L, "6", TimeTableAction.FILE_TRANSFER.toString(), JobState.TIMEOUT, "corr-id-3", now.plus(2, ChronoUnit.MINUTES), "ost");
        repository.save(sReimport);


        List<JobEvent> statusList = repository.getLatestTimetableFileTransfer(3L);
        Assertions.assertEquals(2, statusList.size());
        Assertions.assertTrue(statusList.containsAll(Arrays.asList(s1, s2)));
    }


    @Test
    void testClearAll() {
        JobEvent s1 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 3L, "1", TimeTableAction.IMPORT.toString(), JobState.OK, "corr-id-1", now, "ost");
        repository.save(s1);
        JobEvent s2 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 4L, "2", TimeTableAction.EXPORT.toString(), JobState.FAILED, "corr-id-1", now.plus(1, ChronoUnit.MINUTES), "ost");
        repository.save(s2);
        JobEvent s3 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file2.zip", 3L, "1", TimeTableAction.IMPORT.toString(), JobState.TIMEOUT, "corr-id-2", now, "ost");
        repository.save(s3);

        repository.clearAll(JobEvent.JobDomain.TIMETABLE.toString());

        Assertions.assertTrue(repository.findTimetableJobEvents(Collections.singletonList(3L), null, null, null, null, null, null).isEmpty());
        Assertions.assertTrue(repository.findTimetableJobEvents(Collections.singletonList(4L), null, null, null, null, null, null).isEmpty());
    }

    @Test
    void testClearForProvider() {
        JobEvent s1 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 3L, "1", TimeTableAction.IMPORT.toString(), JobState.OK, "corr-id-1", now, "ost");
        repository.save(s1);
        JobEvent s2 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file1.zip", 4L, "2", TimeTableAction.EXPORT.toString(), JobState.FAILED, "corr-id-1", now.plus(1, ChronoUnit.MINUTES), "ost");
        repository.save(s2);
        JobEvent s3 = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file2.zip", 3L, "1", TimeTableAction.IMPORT.toString(), JobState.TIMEOUT, "corr-id-2", now, "ost");
        repository.save(s3);

        repository.clear(JobEvent.JobDomain.TIMETABLE.toString(), 3L);

        Assertions.assertTrue(repository.findTimetableJobEvents(Collections.singletonList(3L), null, null, null, null, null, null).isEmpty());
        Assertions.assertEquals(1, repository.findTimetableJobEvents(Collections.singletonList(4L), null, null, null, null, null, null).size());
    }

    @Test
    void findCrudEventsAllParamsSet() {

        final Instant refTime = LocalDateTime.of(2019, 2, 19, 16, 19, 0)
                .atOffset(ZoneOffset.UTC)
                .toInstant();

        CrudEvent crudEvent = CrudEvent.builder()
                .entityClassifier("class")
                .changeType("changeType")
                .entityType("entityType")
                .version(1L).comment("comm")
                .action("CREATE").externalId("213").eventTime(refTime).build();

        CrudEvent savedCrudEvent = repository.save(crudEvent);
        CrudEventSearch search =
                new CrudEventSearch(crudEvent.getUsername(),
                        crudEvent.getEntityType(),
                        crudEvent.getEntityClassifier(),
                        crudEvent.getAction(),
                        crudEvent.getExternalId(),
                        crudEvent.getEventTime().minusSeconds(20),
                        refTime);

        List<CrudEvent> crudEvents = repository.findCrudEvents(search);
        Assertions.assertEquals(1, crudEvents.size());
        Assertions.assertEquals(savedCrudEvent.getPk(), crudEvents.get(0).getPk());
    }
}
