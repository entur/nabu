package no.rutebanken.nabu.organisation.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Notification  {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long pk;

	@Enumerated(EnumType.STRING)
	@NotNull
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

		if (notificationType != that.notificationType) return false;
		return trigger != null ? trigger.equals(that.trigger) : that.trigger == null;
	}

	@Override
	public int hashCode() {
		int result = notificationType != null ? notificationType.hashCode() : 0;
		result = 31 * result + (trigger != null ? trigger.hashCode() : 0);
		return result;
	}
}
