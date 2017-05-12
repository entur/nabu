package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Log notifications to user (for test purposes).
 */
@Service
public class LoggingEmailNotificationSender implements NotificationSender {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sendNotificationsForUser(User user, Set<Event> events) {

        if (user.getContactDetails() == null) {
            logger.warn("Unable to notify user without contact details");
            return;
        }

        logger.info("Notifying user: " + user + " of events: " + events);
    }

    @Override
    public NotificationType getSupportedNotificationType() {
        return NotificationType.EMAIL;
    }
}
