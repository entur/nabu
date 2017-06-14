package no.rutebanken.nabu.organisation.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.rutebanken.nabu.organisation.model.user.NotificationType;

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
}
