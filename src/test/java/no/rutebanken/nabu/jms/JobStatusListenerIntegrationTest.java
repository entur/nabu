package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.time.Instant;

public class JobStatusListenerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JobEventListener eventListener;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;

    @Test
    public void jobEventUpdatesSystemJobStatus() {
        JobEventDTO firstPendingEvent = createEvent(JobState.PENDING, Instant.now());
        eventListener.processMessage(toJson(firstPendingEvent));
        assertSystemJobStatus(firstPendingEvent);

        JobEventDTO firstFailedEvent = createEvent(JobState.FAILED, Instant.now().plusMillis(1000));
        eventListener.processMessage(toJson(firstFailedEvent));
        assertSystemJobStatus(firstFailedEvent);

        JobEventDTO secondFailedEvent = createEvent(JobState.FAILED, Instant.now().plusMillis(2000));
        eventListener.processMessage(toJson(secondFailedEvent));
        assertSystemJobStatus(secondFailedEvent);


        // Old started event should not affect state
        JobEventDTO secondPendingEvent = createEvent(JobState.PENDING, Instant.now().minusMillis(1000));
        eventListener.processMessage(toJson(secondPendingEvent));
        assertSystemJobStatus(firstPendingEvent);


        Assert.assertEquals(4, eventRepository.findAll().size());

        JobEvent queryEvent=JobEvent.builder().domain(firstPendingEvent.domain).build();
        queryEvent.setRegisteredTime(null);
        Assert.assertEquals(4, eventRepository.findAll(Example.of(queryEvent)).size());

    }

    protected void assertSystemJobStatus(JobEventDTO jobEvent) {
        SystemJobStatus systemJobStatus = systemJobStatusRepository.findByJobDomainAndActionAndState(jobEvent.domain,
                jobEvent.action, jobEvent.state);

        Assert.assertEquals(jobEvent.eventTime, systemJobStatus.getLastStatusTime());
        Assert.assertEquals(jobEvent.state, systemJobStatus.getState());
    }

    protected JobEventDTO createEvent(JobState state, Instant time) {
        JobEventDTO jobEvent = new JobEventDTO();
        jobEvent.eventTime = time;
        jobEvent.state = state;
        jobEvent.action = "action";
        jobEvent.domain = "JobStatusListener";
        return jobEvent;
    }


}
