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

package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.rest.domain.JobStatus;
import no.rutebanken.nabu.rest.internal.TimeTableJobEventResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class TimeTableJobEventResourceTest {

    private static final String ACTION1="IMPORT";
    private static final String ACTION2="EXPORT";
    public static final String EXTERNAL_ID_1 = "1";
    public static final String EXTERNAL_ID_2 = "2";

    @Test
    void testGetStatusForProvider() {

        List<JobEvent> rawEvents = new ArrayList<>();

        Instant t0 = Instant.now().minusMillis(2000);

        // Job "b" -> OK
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename2", 2L, null, ACTION2, JobState.PENDING, "b", t0.plusMillis(4), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename2", 2L, EXTERNAL_ID_1, ACTION2, JobState.STARTED, "b", t0.plusMillis(5), "pb"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename2", 2L, EXTERNAL_ID_1, ACTION2, JobState.OK, "b", t0.plusMillis(6), "pb"));

        // Job "a" -> FAILED
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename1", 2L, null, ACTION1, JobState.PENDING, "a", t0.plusMillis(1), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename1", 2L, EXTERNAL_ID_2, ACTION1, JobState.STARTED, "a", t0.plusMillis(2), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename1", 2L, EXTERNAL_ID_2, ACTION1, JobState.FAILED, "a", t0.plusMillis(3), "ost"));


        List<JobStatus> listStatus = new TimeTableJobEventResource(null, null).convert(rawEvents);

        Assertions.assertNotNull(listStatus);
        Assertions.assertEquals(2, listStatus.size());

        JobStatus a = listStatus.getFirst();

        Assertions.assertEquals("a", a.getCorrelationId());
        Assertions.assertEquals(ACTION1, a.getEvents().getFirst().action);
        Assertions.assertEquals(JobStatus.State.FAILED, a.getEndStatus());
        Assertions.assertEquals(3, a.getEvents().size());
        Assertions.assertEquals(Date.from(t0.plusMillis(1)), a.getFirstEvent());
        Assertions.assertEquals(Date.from(t0.plusMillis(3)), a.getLastEvent());

        Assertions.assertEquals(EXTERNAL_ID_2, a.getEvents().get(1).chouetteJobId);

        JobStatus b = listStatus.get(1);

        Assertions.assertEquals("b", b.getCorrelationId());
        Assertions.assertEquals(ACTION2, b.getEvents().getFirst().action);
        Assertions.assertEquals(JobStatus.State.OK, b.getEndStatus());
        Assertions.assertEquals(3, b.getEvents().size());
        Assertions.assertEquals(Date.from(t0.plusMillis(4)), b.getFirstEvent());
        Assertions.assertEquals(Date.from(t0.plusMillis(6)), b.getLastEvent());

        Assertions.assertEquals("ost", b.getEvents().get(0).referential);
        Assertions.assertEquals(EXTERNAL_ID_1, b.getEvents().get(1).chouetteJobId);
        Assertions.assertEquals("pb", b.getEvents().get(1).referential);
        Assertions.assertEquals(EXTERNAL_ID_1, b.getEvents().get(2).chouetteJobId);
        Assertions.assertEquals("pb", b.getEvents().get(2).referential);
    }

    /**
     * A service (e.g. servicelinker) emits STARTED then SUCCESS, but the relayed event times can
     * arrive inverted when Pub/Sub delivers the two messages out of order. The OK event then has an
     * earlier event time than the STARTED event. The aggregation must still end in OK, with the
     * stale STARTED dropped, rather than appearing stuck at STARTED.
     */
    @Test
    void staleStartedAfterTerminalIsDropped() {

        List<JobEvent> rawEvents = new ArrayList<>();

        Instant t0 = Instant.now().minusMillis(2000);

        String action = "LINKING";
        // event times are inverted: OK precedes STARTED
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename", 2L, null, action, JobState.PENDING, "c", t0.plusMillis(1), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename", 2L, null, action, JobState.OK, "c", t0.plusMillis(2), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename", 2L, null, action, JobState.STARTED, "c", t0.plusMillis(3), "ost"));

        List<JobStatus> listStatus = new TimeTableJobEventResource(null, null).convert(rawEvents);

        Assertions.assertEquals(1, listStatus.size());
        JobStatus c = listStatus.getFirst();
        Assertions.assertEquals(JobStatus.State.OK, c.getEndStatus());
        Assertions.assertEquals(2, c.getEvents().size());
        Assertions.assertEquals(JobStatus.State.OK, c.getEvents().getLast().state);
    }

    /**
     * Same inversion as above, but for the FAILED terminal path: a stale STARTED arriving after a
     * FAILED must not resurrect the action to "Started", and must not clobber the error code with
     * the STARTED event's null.
     */
    @Test
    void staleStartedAfterFailedIsDroppedAndErrorCodePreserved() {

        List<JobEvent> rawEvents = new ArrayList<>();

        Instant t0 = Instant.now().minusMillis(2000);

        String action = "LINKING";
        JobEvent failed = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename", 2L, null, action, JobState.FAILED, "d", t0.plusMillis(2), "ost");
        failed.setErrorCode("OSRM timeout");

        // event times are inverted: FAILED precedes STARTED
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename", 2L, null, action, JobState.PENDING, "d", t0.plusMillis(1), "ost"));
        rawEvents.add(failed);
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename", 2L, null, action, JobState.STARTED, "d", t0.plusMillis(3), "ost"));

        List<JobStatus> listStatus = new TimeTableJobEventResource(null, null).convert(rawEvents);

        Assertions.assertEquals(1, listStatus.size());
        JobStatus d = listStatus.getFirst();
        Assertions.assertEquals(JobStatus.State.FAILED, d.getEndStatus());
        Assertions.assertEquals(2, d.getEvents().size());
        Assertions.assertEquals(JobStatus.State.FAILED, d.getEvents().getLast().state);
        Assertions.assertEquals("OSRM timeout", d.getErrorCode());
    }


}