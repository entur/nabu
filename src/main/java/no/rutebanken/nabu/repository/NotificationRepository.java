package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {


    /**
     * Clear all notifications for events for a given job domain.
     */
    void clearAll(String domain);


    /**
     * Clear all notifications for events for a given job domain and provider combination.
     */
    void clear(String domain, Long providerId);
}
