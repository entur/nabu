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

package no.rutebanken.nabu.rest.internal;


import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import static no.rutebanken.nabu.event.support.DateUtils.atDefaultZone;

class DataDeliveryTimeTableJobEventResourceTest {

    private static final String JOB_DOMAIN = JobEvent.JobDomain.TIMETABLE.toString();
    private static final Instant NOW = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    void testMapToDataDeliveryJobEventEmptyList() {
        DataDeliveryStatus dataDeliveryJobEvent = new LatestUploadResource(null).toDataDeliveryStatus(new ArrayList<>());
        Assertions.assertNull(dataDeliveryJobEvent.date);
        Assertions.assertNull(dataDeliveryJobEvent.state);
    }

    @Test
    void testMapToDataDeliveryJobEventSuccess() {
        JobEvent s1 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.OK, "corr-id-1", NOW, "ost");
        JobEvent s2 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.OTP2_BUILD_GRAPH.toString(), JobState.OK, "corr-id-1", NOW.plusMillis(1000), "ost");
        JobEvent s3 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.EXPORT_NETEX.toString(), JobState.PENDING, "corr-id-1", NOW.plusMillis(2000), "ost");
        DataDeliveryStatus dataDeliveryJobEvent = new LatestUploadResource(null).toDataDeliveryStatus(Arrays.asList(s1, s2, s3));
        Assertions.assertEquals(atDefaultZone(s1.getEventTime()), dataDeliveryJobEvent.date);
        Assertions.assertEquals(DataDeliveryStatus.State.OK, dataDeliveryJobEvent.state);
    }

    @Test
    void testMapToDataDeliveryJobEventInProgress() {

        JobEvent s1 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.OK, "corr-id-1", NOW, "ost");
        JobEvent s2 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.OTP2_BUILD_GRAPH.toString(), JobState.STARTED, "corr-id-1", NOW.plusMillis(1000), "ost");
        JobEvent s3 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.EXPORT_NETEX.toString(), JobState.OK, "corr-id-1", NOW.plusMillis(2000), "ost");
        DataDeliveryStatus dataDeliveryJobEvent = new LatestUploadResource(null).toDataDeliveryStatus(Arrays.asList(s1, s2, s3));
        Assertions.assertEquals(atDefaultZone(s1.getEventTime()), dataDeliveryJobEvent.date);
        Assertions.assertEquals(DataDeliveryStatus.State.IN_PROGRESS, dataDeliveryJobEvent.state);
    }

    @Test
    void testMapToDataDeliveryJobEventFailed() {
        JobEvent s1 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.OK, "corr-id-1", NOW, "ost");
        JobEvent s2 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_CLASSIFICATION.toString(), JobState.FAILED, "corr-id-1", NOW.plusMillis(1000), "ost");
        DataDeliveryStatus dataDeliveryJobEvent = new LatestUploadResource(null).toDataDeliveryStatus(Arrays.asList(s1, s2));
        Assertions.assertEquals(atDefaultZone(s1.getEventTime()), dataDeliveryJobEvent.date);
        Assertions.assertEquals(DataDeliveryStatus.State.FAILED, dataDeliveryJobEvent.state);
    }

}
