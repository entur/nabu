package no.rutebanken.nabu.jms;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.UserNotificationEventHandler;
import no.rutebanken.nabu.event.user.UserRepository;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import no.rutebanken.nabu.event.user.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;


public class EventNotificationIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private JobEventListener eventListener;

    @Autowired
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepositoryMock;

    @Autowired
    private UserNotificationEventHandler userNotificationEventHandler;

    @Before
    public void setUp() throws Exception {
        userNotificationEventHandler.setUserRepository(userRepositoryMock);
    }

    @Test
    public void eventsTriggerNotifications() {
        String activeFilterAction = "active";
        String inactiveFilterAction = "inActive";

        Set<NotificationConfigDTO> config = Sets.newHashSet(new NotificationConfigDTO(NotificationType.WEB, false, jobEventFilter(inactiveFilterAction, JobState.FAILED)),
                new NotificationConfigDTO(NotificationType.WEB, true, jobEventFilter(activeFilterAction, JobState.FAILED)));

        UserDTO user = new UserDTO();
        user.username = "username";
        user.notifications = config;

        when(userRepositoryMock.findAll()).thenReturn(Arrays.asList(user));

        // Matching action, but not state
        JobEventDTO notMatchingDifferentState = createEvent(JobState.PENDING, activeFilterAction, Instant.now());
        eventListener.processMessage(toJson(notMatchingDifferentState));

        // Matching state for inactive filter
        JobEventDTO actionForInactiveFilter = createEvent(JobState.FAILED, inactiveFilterAction, Instant.now().plusMillis(1000));
        eventListener.processMessage(toJson(actionForInactiveFilter));

        // Matching event for active filter
        JobEventDTO matchingEvent = createEvent(JobState.FAILED, activeFilterAction, Instant.now().plusMillis(2000));
        eventListener.processMessage(toJson(matchingEvent));

        List<Notification> notifications = notificationRepository.findByUserNameAndTypeAndStatus(user.getUsername(), NotificationType.WEB, Notification.NotificationStatus.READY);

        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(notifications.get(0).getEvent().getEventTime(), matchingEvent.eventTime);
    }

    private EventFilterDTO jobEventFilter(String action, JobState jobState) {
        EventFilterDTO eventFilter = new EventFilterDTO();
        eventFilter.type = EventFilterDTO.EventFilterType.JOB;
        eventFilter.jobDomain = JobEvent.JobDomain.TIMETABLE.toString();
        eventFilter.actions = Sets.newHashSet(action);
        eventFilter.states = Sets.newHashSet(jobState);
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
