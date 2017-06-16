package no.rutebanken.nabu.event;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ImmediateNotificationService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Map<NotificationType, NotificationProcessor> notificationSenders;

    public void sendNotifications(Notification notification, User user) {
        NotificationType type = notification.getType();
        logger.info("About to send notifications of type: " + type + " to user " + user.getUsername());
        NotificationProcessor notificationSender = notificationSenders.get(type);
        if (notificationSender == null) {
            throw new IllegalArgumentException("No notification sender registered for notification type: " + type);
        }

        notificationSender.processNotificationsForUser(user, Sets.newHashSet(notification));
    }

}
