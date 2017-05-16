package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.organisation.model.user.NotificationConfiguration;
import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.repository.UserRepository;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Create notifications for user with configured events filter matching a events.
 */
@Service
public class UserNotificationEventHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void onEvent(Event event) {
        userRepository.findAll().forEach(user -> createNotificationsForUser(user, event));
    }

    void createNotificationsForUser(User user, Event event) {

        if (CollectionUtils.isEmpty(user.getNotificationConfigurations())) {
            return;
        }

        user.getNotificationConfigurations().stream().filter(notificationConfig -> notificationConfig.getEventFilter().getMatcher().matches(event))
                .forEach(notificationConfig -> createNotification(user, notificationConfig, event));

    }

    private void createNotification(User user, NotificationConfiguration notificationConfig, Event event) {
        Notification notification = new Notification(user.getUsername(), notificationConfig.getNotificationType(), event);
        logger.info("Registered new notification: " + notification);
        notificationRepository.save(notification);
    }


}
