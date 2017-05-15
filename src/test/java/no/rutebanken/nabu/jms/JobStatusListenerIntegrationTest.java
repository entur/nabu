package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.NotificationRepository;
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

    @Autowired
    private NotificationRepository notificationRepository;


    @Test
    public void jobEventUpdatesSystemJobStatus() {
        JobEventDTO pendingEvent = createEvent(JobState.PENDING, Instant.now());
        eventListener.processMessage(toJson(pendingEvent));
        assertSystemJobStatus(pendingEvent);

        JobEventDTO failedEvent = createEvent(JobState.FAILED, Instant.now().plusMillis(1000));
        eventListener.processMessage(toJson(failedEvent));
        assertSystemJobStatus(failedEvent);

        // Old started event should not affect state
        JobEventDTO startedEvent = createEvent(JobState.STARTED, Instant.now().minusMillis(1000));
        eventListener.processMessage(toJson(startedEvent));
        assertSystemJobStatus(failedEvent);


        Assert.assertEquals(3, eventRepository.findAll().size());

        JobEvent queryEvent=JobEvent.builder().domain(pendingEvent.domain).build();
        queryEvent.setRegisteredTime(null);
        Assert.assertEquals(3, eventRepository.findAll(Example.of(queryEvent)).size());

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
