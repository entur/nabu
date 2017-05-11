package no.rutebanken.nabu.notification;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.User;

import java.util.Set;

public interface NotificationSender {

    void sendNotificationsForUser(User user, Set<Event> events);

    NotificationType getSupportedNotificationType();

}
