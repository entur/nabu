package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;

import java.util.Set;

public interface NotificationProcessor {

    /**
     * Handle a set not notifications for a user.
     *
     * Processor is expected to delete/mark as read notifications in a manner transactionally suitable to the processing.
     */
    void processNotificationsForUser(UserDTO user, Set<Notification> notifications);

    Set<NotificationType> getSupportedNotificationTypes();

}
