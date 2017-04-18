package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DataDeliveryStatusResourceTest {

    @Test
    public void testMapToDataDeliveryStatusEmptyList() throws Exception {
        DataDeliveryStatus dataDeliveryStatus = new DataDeliveryStatusResource().toDataDeliveryStatus(new ArrayList<>());
        Assert.assertNull(dataDeliveryStatus.date);
        Assert.assertNull(dataDeliveryStatus.state);
    }

    @Test
    public void testMapToDataDeliveryStatusSuccess() throws Exception {
        Status s1 = new Status("file1.zip", 3L, 1L, Status.Action.FILE_TRANSFER, Status.State.OK, "corr-id-1", new Date(0), "ost");
        Status s2 = new Status("file1.zip", 3L, 1L, Status.Action.BUILD_GRAPH, Status.State.OK, "corr-id-1", new Date(1000), "ost");
        Status s3 = new Status("file1.zip", 3L, 1L, Status.Action.EXPORT_NETEX, Status.State.PENDING, "corr-id-1", new Date(2000), "ost");
        DataDeliveryStatus dataDeliveryStatus = new DataDeliveryStatusResource().toDataDeliveryStatus(Arrays.asList(s1, s2, s3));
        Assert.assertEquals(s1.date, dataDeliveryStatus.date);
        Assert.assertEquals(DataDeliveryStatus.State.OK, dataDeliveryStatus.state);
    }

    @Test
    public void testMapToDataDeliveryStatusInProgress() throws Exception {
        Status s1 = new Status("file1.zip", 3L, 1L, Status.Action.FILE_TRANSFER, Status.State.OK, "corr-id-1", new Date(0), "ost");
        Status s2 = new Status("file1.zip", 3L, 1L, Status.Action.BUILD_GRAPH, Status.State.STARTED, "corr-id-1", new Date(1000), "ost");
        Status s3 = new Status("file1.zip", 3L, 1L, Status.Action.EXPORT_NETEX, Status.State.OK, "corr-id-1", new Date(2000), "ost");
        DataDeliveryStatus dataDeliveryStatus = new DataDeliveryStatusResource().toDataDeliveryStatus(Arrays.asList(s1, s2, s3));
        Assert.assertEquals(s1.date, dataDeliveryStatus.date);
        Assert.assertEquals(DataDeliveryStatus.State.IN_PROGRESS, dataDeliveryStatus.state);
    }

    @Test
    public void testMapToDataDeliveryStatusFailed() throws Exception {
        Status s1 = new Status("file1.zip", 3L, 1L, Status.Action.FILE_TRANSFER, Status.State.OK, "corr-id-1", new Date(0), "ost");
        Status s2 = new Status("file1.zip", 3L, 1L, Status.Action.FILE_CLASSIFICATION, Status.State.FAILED, "corr-id-1", new Date(1000), "ost");
        DataDeliveryStatus dataDeliveryStatus = new DataDeliveryStatusResource().toDataDeliveryStatus(Arrays.asList(s1, s2));
        Assert.assertEquals(s1.date, dataDeliveryStatus.date);
        Assert.assertEquals(DataDeliveryStatus.State.FAILED, dataDeliveryStatus.state);
    }
}
