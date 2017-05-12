package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {

}
