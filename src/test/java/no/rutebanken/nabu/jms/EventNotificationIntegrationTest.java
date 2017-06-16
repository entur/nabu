package no.rutebanken.nabu.jms;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
import no.rutebanken.nabu.organisation.TestConstantsOrganisation;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
import no.rutebanken.nabu.organisation.rest.ResourceTestUtils;
import no.rutebanken.nabu.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class EventNotificationIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private JobEventListener eventListener;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void eventsTriggerNotifications() {
        String activeFilterAction = "active";
        String inactiveFilterAction = "inActive";

        Set<NotificationConfigDTO> config = Sets.newHashSet(new NotificationConfigDTO(NotificationType.WEB, false, jobEventFilter(inactiveFilterAction, JobState.FAILED)),
                new NotificationConfigDTO(NotificationType.WEB, true, jobEventFilter(activeFilterAction, JobState.FAILED)));

        ResourceTestUtils.setNotificationConfig(restTemplate, TestConstantsOrganisation.USER_USERNAME, config);


        // Matching action, but not state
        JobEventDTO notMatchingDifferentState = createEvent(JobState.PENDING, activeFilterAction, Instant.now());
        eventListener.processMessage(toJson(notMatchingDifferentState));

        // Matching state for inactive filter
        JobEventDTO actionForInactiveFilter = createEvent(JobState.FAILED, inactiveFilterAction, Instant.now().plusMillis(1000));
        eventListener.processMessage(toJson(actionForInactiveFilter));

        // Matching event for active filter
        JobEventDTO matchingEvent = createEvent(JobState.FAILED, activeFilterAction, Instant.now().plusMillis(2000));
        eventListener.processMessage(toJson(matchingEvent));

        List<Notification> notifications = notificationRepository.findByUserNameAndTypeAndStatus(TestConstantsOrganisation.USER_USERNAME, NotificationType.WEB, Notification.NotificationStatus.READY);

        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(notifications.get(0).getEvent().getEventTime(), matchingEvent.eventTime);
    }

    private EventFilterDTO jobEventFilter(String action, JobState jobState) {
        EventFilterDTO eventFilterDTO = new EventFilterDTO(EventFilterDTO.EventFilterType.JOB);
        eventFilterDTO.actions = Sets.newHashSet(action);
        eventFilterDTO.jobDomain = JobEvent.JobDomain.TIMETABLE;
        eventFilterDTO.states = Sets.newHashSet(jobState);
        return eventFilterDTO;
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
