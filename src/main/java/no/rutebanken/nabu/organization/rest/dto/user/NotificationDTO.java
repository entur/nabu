package no.rutebanken.nabu.organization.rest.dto.user;

public class NotificationDTO {

	public enum NotificationType {EMAIL}

	public NotificationType notificationType;

	public String trigger;

	public NotificationDTO(NotificationType notificationType, String trigger) {
		this.notificationType = notificationType;
		this.trigger = trigger;
	}

	public NotificationDTO() {
	}
}
