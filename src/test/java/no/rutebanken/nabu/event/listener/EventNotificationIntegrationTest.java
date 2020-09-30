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
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.UserNotificationEventHandler;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import no.rutebanken.nabu.event.user.UserRepository;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import no.rutebanken.nabu.event.user.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;


public class EventNotificationIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private JobEventProcessor jobEventProcessor;

    @Autowired
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepositoryMock;

    @Autowired
    private UserNotificationEventHandler userNotificationEventHandler;

    @BeforeEach
    public void setUp() {
        userNotificationEventHandler.setUserRepository(userRepositoryMock);
    }

    @Test
    public void eventsTriggerNotifications() {
        String activeFilterAction = "active";
        String inactiveFilterAction = "inActive";

        Set<NotificationConfigDTO> config = Set.of(new NotificationConfigDTO(NotificationType.WEB, false, jobEventFilter(inactiveFilterAction, JobState.FAILED)),
                new NotificationConfigDTO(NotificationType.WEB, true, jobEventFilter(activeFilterAction, JobState.FAILED)));

        UserDTO user = new UserDTO();
        user.username = "username";
        user.notifications = config;

        when(userRepositoryMock.findAll()).thenReturn(Collections.singletonList(user));

        // Matching action, but not state
        JobEventDTO notMatchingDifferentState = createEvent(JobState.PENDING, activeFilterAction, Instant.now());
        jobEventProcessor.processMessage(toJson(notMatchingDifferentState));

        // Matching state for inactive filter
        JobEventDTO actionForInactiveFilter = createEvent(JobState.FAILED, inactiveFilterAction, Instant.now().plusMillis(1000));
        jobEventProcessor.processMessage(toJson(actionForInactiveFilter));

        // Matching event for active filter
        JobEventDTO matchingEvent = createEvent(JobState.FAILED, activeFilterAction, Instant.now().plusMillis(2000));
        jobEventProcessor.processMessage(toJson(matchingEvent));

        List<Notification> notifications = notificationRepository.findByUserNameAndTypeAndStatus(user.getUsername(), NotificationType.WEB, Notification.NotificationStatus.READY);

        Assertions.assertEquals(1, notifications.size());
        Assertions.assertEquals(notifications.get(0).getEvent().getEventTime(), matchingEvent.eventTime);
    }

    private EventFilterDTO jobEventFilter(String action, JobState jobState) {
        EventFilterDTO eventFilter = new EventFilterDTO();
        eventFilter.type = EventFilterDTO.EventFilterType.JOB;
        eventFilter.jobDomain = JobEvent.JobDomain.TIMETABLE.toString();
        eventFilter.actions = Set.of(action);
        eventFilter.states = Set.of(jobState);
        return eventFilter;
    }


    protected JobEventDTO createEvent(JobState state, String action, Instant time) {
        JobEventDTO jobEvent = new JobEventDTO();
        jobEvent.eventTime = time;
        jobEvent.state = state;
        jobEvent.action = action;
        jobEvent.domain = JobEvent.JobDomain.TIMETABLE.toString();
        return jobEvent;
    }
}
