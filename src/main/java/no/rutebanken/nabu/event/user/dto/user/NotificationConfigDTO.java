package no.rutebanken.nabu.event.user.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.rutebanken.nabu.domain.event.NotificationType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationConfigDTO {

    public NotificationType notificationType;

    public EventFilterDTO eventFilter;

    public boolean enabled = true;

    public NotificationConfigDTO(NotificationType notificationType, boolean enabled, EventFilterDTO eventFilter) {
        this.notificationType = notificationType;
        this.enabled = enabled;
        this.eventFilter = eventFilter;
    }

    public NotificationConfigDTO() {
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public EventFilterDTO getEventFilter() {
        return eventFilter;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
