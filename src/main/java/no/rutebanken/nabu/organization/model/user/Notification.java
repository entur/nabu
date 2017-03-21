package no.rutebanken.nabu.organization.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Notification  {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long pk;

	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	// TODO Define trigger types
	private String trigger;


	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Notification that = (Notification) o;

		return notificationType == that.notificationType;
	}

	@Override
	public int hashCode() {
		return notificationType.hashCode();
	}
}
