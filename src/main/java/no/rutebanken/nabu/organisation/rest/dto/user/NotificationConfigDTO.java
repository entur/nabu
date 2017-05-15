package no.rutebanken.nabu.organisation.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.rutebanken.nabu.organisation.model.user.NotificationType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationConfigDTO {

    public NotificationType notificationType;

    public EventFilterDTO eventFilter;

    public NotificationConfigDTO(NotificationType notificationType, EventFilterDTO eventFilter) {
        this.notificationType = notificationType;
        this.eventFilter = eventFilter;
    }

    public NotificationConfigDTO() {
    }
}
