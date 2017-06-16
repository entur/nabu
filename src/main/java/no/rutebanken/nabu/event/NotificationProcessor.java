package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.User;

import java.util.Set;

public interface NotificationProcessor {

    /**
     * Handle a set not notifications for a user.
     *
     * Processor is expected to delete/mark as read notifications in a manner transactionally suitable to the processing.
     */
    void processNotificationsForUser(User user, Set<Notification> notifications);

    Set<NotificationType> getSupportedNotificationTypes();

}
