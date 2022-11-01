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

package no.rutebanken.nabu.event.listener;

import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.UserNotificationEventHandler;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import no.rutebanken.nabu.event.user.UserRepository;
import no.rutebanken.nabu.exceptions.NabuException;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

import static org.mockito.Mockito.when;

class JobStatusListenerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JobEventProcessor jobEventProcessor;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;

    @Mock
    private UserRepository userRepositoryMock;

    @Autowired
    private UserNotificationEventHandler userNotificationEventHandler;

    @BeforeEach
    void setUp() {
        userNotificationEventHandler.setUserRepository(userRepositoryMock);
        when(userRepositoryMock.findAll()).thenReturn(new ArrayList<>());
    }


    @Test
    void jobEventUpdatesSystemJobStatus() {
        Instant now = Instant.now();
        JobEventDTO firstPendingEvent = createEvent(JobState.PENDING, now);
        jobEventProcessor.processMessage(toJson(firstPendingEvent));
        assertSystemJobStatus(firstPendingEvent);

        JobEventDTO firstFailedEvent = createEvent(JobState.FAILED, now.plusMillis(1000));
        jobEventProcessor.processMessage(toJson(firstFailedEvent));
        assertSystemJobStatus(firstFailedEvent);

        JobEventDTO secondFailedEvent = createEvent(JobState.FAILED, now.plusMillis(2000));
        jobEventProcessor.processMessage(toJson(secondFailedEvent));
        assertSystemJobStatus(secondFailedEvent);


        // Old started event should not affect state
        JobEventDTO secondPendingEvent = createEvent(JobState.PENDING, now.minusMillis(1000));
        jobEventProcessor.processMessage(toJson(secondPendingEvent));
        assertSystemJobStatus(firstPendingEvent);


        Assertions.assertEquals(4, eventRepository.findAll().size());

        JobEvent queryEvent = JobEvent.builder().domain(firstPendingEvent.getDomain()).build();
        Assertions.assertEquals(4, eventRepository.findAll(Example.of(queryEvent, ExampleMatcher.matching().withIgnorePaths("registeredTime"))).size());

    }

    @Test
    // Use propagation = never so that the transaction boundaries are within jobEventProcessor.processMessage()
    @Transactional(propagation = Propagation.NEVER)
    void saveInvalidJobEvent() {
        Instant now = Instant.now();
        JobEventDTO invalidEvent = createEvent(JobState.PENDING, now, "X".repeat(500), "domain");
        String json = toJson(invalidEvent);
        Assertions.assertThrows(NabuException.class, () -> jobEventProcessor.processMessage(json));
    }


        protected void assertSystemJobStatus(JobEventDTO jobEvent) {
        SystemJobStatus systemJobStatus = systemJobStatusRepository.findByJobDomainAndActionAndState(jobEvent.getDomain(),
                jobEvent.getAction(), jobEvent.getState());

        Assertions.assertEquals(jobEvent.getEventTime(), systemJobStatus.getLastStatusTime());
        Assertions.assertEquals(jobEvent.getState(), systemJobStatus.getState());
    }

    protected JobEventDTO createEvent(JobState state, Instant time) {
        return createEvent(state, time, "action", "JobStatusListener");
    }

    protected JobEventDTO createEvent(JobState state, Instant time, String action, String domain) {
        JobEventDTO jobEvent = new JobEventDTO();
        jobEvent.setEventTime(time);
        jobEvent.setState(state);
        jobEvent.setAction(action);
        jobEvent.setDomain(domain);
        return jobEvent;
    }


}
