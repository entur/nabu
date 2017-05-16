package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByTypeAndStatus(NotificationType notificationType, Notification.NotificationStatus status);

    List<Notification> findByUserNameAndTypeAndStatus(String userName, NotificationType notificationType, Notification.NotificationStatus status);


    /**
     * Clear all notifications for events for a given job domain.
     */
    void clearAll(String domain);


    /**
     * Clear all notifications for events for a given job domain and provider combination.
     */
    void clear(String domain, Long providerId);
}
