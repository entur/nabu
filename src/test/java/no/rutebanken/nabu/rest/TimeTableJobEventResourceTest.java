package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.rest.domain.JobStatus;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeTableJobEventResourceTest {

    private static final String ACTION1="IMPORT";
    private static final String ACTION2="EXPORT";
    
    @Test
    public void testGetStatusForProvider() throws Exception {

        List<JobEvent> rawEvents = new ArrayList<>();

        Instant t0 = Instant.now().minusMillis(2000);

        // Job "b" -> OK
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename2", 2l, null, ACTION2, JobState.PENDING, "b", t0.plusMillis(4), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename2", 2l, "1", ACTION2, JobState.STARTED, "b", t0.plusMillis(5), "pb"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename2", 2l, "1", ACTION2, JobState.OK, "b", t0.plusMillis(6), "pb"));

        // Job "a" -> FAILED
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename1", 2l, null, ACTION1, JobState.PENDING, "a", t0.plusMillis(1), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename1", 2l, "2", ACTION1, JobState.STARTED, "a", t0.plusMillis(2), "ost"));
        rawEvents.add(new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "filename1", 2l, "2", ACTION1, JobState.FAILED, "a", t0.plusMillis(3), "ost"));


        List<JobStatus> listStatus = new TimeTableJobEventResource().convert(rawEvents);

        Assert.assertNotNull(listStatus);
        Assert.assertEquals(2, listStatus.size());

        JobStatus a = listStatus.get(0);

        Assert.assertEquals("a", a.getCorrelationId());
        Assert.assertEquals(ACTION1, a.getEvents().get(0).action);
        Assert.assertEquals(JobStatus.State.FAILED, a.getEndStatus());
        Assert.assertEquals(3, a.getEvents().size());
        Assert.assertEquals(Date.from(t0.plusMillis(1)), a.getFirstEvent());
        Assert.assertEquals(Date.from(t0.plusMillis(3)), a.getLastEvent());

        Assert.assertEquals(Long.valueOf(2), a.getEvents().get(1).chouetteJobId);

        JobStatus b = listStatus.get(1);

        Assert.assertEquals("b", b.getCorrelationId());
        Assert.assertEquals(ACTION2, b.getEvents().get(0).action);
        Assert.assertEquals(JobStatus.State.OK, b.getEndStatus());
        Assert.assertEquals(3, b.getEvents().size());
        Assert.assertEquals(Date.from(t0.plusMillis(4)), b.getFirstEvent());
        Assert.assertEquals(Date.from(t0.plusMillis(6)), b.getLastEvent());

        Assert.assertEquals("ost", b.getEvents().get(0).referential);
        Assert.assertEquals(Long.valueOf(1), b.getEvents().get(1).chouetteJobId);
        Assert.assertEquals("pb", b.getEvents().get(1).referential);
        Assert.assertEquals(Long.valueOf(1), b.getEvents().get(2).chouetteJobId);
        Assert.assertEquals("pb", b.getEvents().get(2).referential);
    }


}