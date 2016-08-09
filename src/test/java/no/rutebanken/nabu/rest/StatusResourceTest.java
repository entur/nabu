package no.rutebanken.nabu.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.domain.Status.Action;
import no.rutebanken.nabu.domain.Status.State;
import no.rutebanken.nabu.rest.domain.JobStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatusResourceTest {

	@Test
	public void testGetStatusForProvider() throws Exception {
		long now = System.currentTimeMillis();
		List<Status> rawEvents = new ArrayList<>();
		// Job "b" -> OK
		rawEvents.add(new Status("filename2", 2l, Action.VALIDATION, State.PENDING, "b", new Date(now + 4)));
		rawEvents.add(new Status("filename2", 2l, Action.VALIDATION, State.STARTED, "b", new Date(now + 5)));
		rawEvents.add(new Status("filename2", 2l, Action.VALIDATION, State.OK, "b", new Date(now + 6)));

		// Job "a" -> FAILED
		rawEvents.add(new Status("filename1", 2l, Action.IMPORT, State.PENDING, "a", new Date(now + 1)));
		rawEvents.add(new Status("filename1", 2l, Action.IMPORT, State.STARTED, "a", new Date(now + 2)));
		rawEvents.add(new Status("filename1", 2l, Action.IMPORT, State.FAILED, "a", new Date(now + 3)));


		List<JobStatus> listStatus = new StatusResource().convert(rawEvents);
		
		Assert.assertNotNull(listStatus);
		Assert.assertEquals(2, listStatus.size());
		
		JobStatus a = listStatus.get(0);
		
		Assert.assertEquals("a", a.getCorrelationId());
		Assert.assertEquals(JobStatus.Action.IMPORT,a.action);
		Assert.assertEquals(JobStatus.State.FAILED,a.getEndStatus());
		Assert.assertEquals(3, a.getEvents().size());
		Assert.assertEquals(new Date(now+1),a.getFirstEvent());
		Assert.assertEquals(new Date(now+3),a.getLastEvent());
		
		JobStatus b = listStatus.get(1);
		
		Assert.assertEquals("b", b.getCorrelationId());
		Assert.assertEquals(JobStatus.Action.VALIDATION,b.action);
		Assert.assertEquals(JobStatus.State.OK,b.getEndStatus());
		Assert.assertEquals(3, b.getEvents().size());
		Assert.assertEquals(new Date(now+4),b.getFirstEvent());
		Assert.assertEquals(new Date(now+6),b.getLastEvent());
		
		
	}

}