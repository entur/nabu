package no.rutebanken.nabu.organisation.rest.dto.user;

public class NotificationConfigDTO {

    public enum NotificationType {EMAIL}

    public NotificationType notificationType;

    public EventFilterDTO eventFilter;

    public NotificationConfigDTO(NotificationType notificationType, EventFilterDTO eventFilter) {
        this.notificationType = notificationType;
        this.eventFilter = eventFilter;
    }

    public NotificationConfigDTO() {
    }
}
