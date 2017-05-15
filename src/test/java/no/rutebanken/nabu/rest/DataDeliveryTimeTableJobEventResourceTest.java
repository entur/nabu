package no.rutebanken.nabu.rest;


import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class DataDeliveryTimeTableJobEventResourceTest {

    private static final String JOB_DOMAIN = JobEvent.JobDomain.TIMETABLE.toString();

    @Test
    public void testMapToDataDeliveryJobEventEmptyList() throws Exception {
        DataDeliveryStatus dataDeliveryJobEvent = new DataDeliveryStatusResource().toDataDeliveryStatus(new ArrayList<>());
        Assert.assertNull(dataDeliveryJobEvent.date);
        Assert.assertNull(dataDeliveryJobEvent.state);
    }

    @Test
    public void testMapToDataDeliveryJobEventSuccess() throws Exception {
        JobEvent s1 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.OK, "corr-id-1", Instant.now(), "ost");
        JobEvent s2 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.BUILD_GRAPH.toString(), JobState.OK, "corr-id-1", Instant.now().plusMillis(1000), "ost");
        JobEvent s3 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.EXPORT_NETEX.toString(), JobState.PENDING, "corr-id-1", Instant.now().plusMillis(2000), "ost");
        DataDeliveryStatus dataDeliveryJobEvent = new DataDeliveryStatusResource().toDataDeliveryStatus(Arrays.asList(s1, s2, s3));
        Assert.assertEquals(s1.getEventTime(), dataDeliveryJobEvent.date.toInstant());
        Assert.assertEquals(DataDeliveryStatus.State.OK, dataDeliveryJobEvent.state);
    }

    @Test
    public void testMapToDataDeliveryJobEventInProgress() throws Exception {
        JobEvent s1 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.OK, "corr-id-1", Instant.now(), "ost");
        JobEvent s2 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.BUILD_GRAPH.toString(), JobState.STARTED, "corr-id-1", Instant.now().plusMillis(1000), "ost");
        JobEvent s3 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.EXPORT_NETEX.toString(), JobState.OK, "corr-id-1", Instant.now().plusMillis(2000), "ost");
        DataDeliveryStatus dataDeliveryJobEvent = new DataDeliveryStatusResource().toDataDeliveryStatus(Arrays.asList(s1, s2, s3));
        Assert.assertEquals(s1.getEventTime(), dataDeliveryJobEvent.date.toInstant());
        Assert.assertEquals(DataDeliveryStatus.State.IN_PROGRESS, dataDeliveryJobEvent.state);
    }

    @Test
    public void testMapToDataDeliveryJobEventFailed() throws Exception {
        JobEvent s1 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_TRANSFER.toString(), JobState.OK, "corr-id-1", Instant.now(), "ost");
        JobEvent s2 = new JobEvent(JOB_DOMAIN, "file1.zip", 3L, "1", TimeTableAction.FILE_CLASSIFICATION.toString(), JobState.FAILED, "corr-id-1", Instant.now().plusMillis(1000), "ost");
        DataDeliveryStatus dataDeliveryJobEvent = new DataDeliveryStatusResource().toDataDeliveryStatus(Arrays.asList(s1, s2));
        Assert.assertEquals(s1.getEventTime(), dataDeliveryJobEvent.date.toInstant());
        Assert.assertEquals(DataDeliveryStatus.State.FAILED, dataDeliveryJobEvent.state);
    }
}
