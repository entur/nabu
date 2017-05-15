package no.rutebanken.nabu.domain.event;

import no.rutebanken.nabu.organisation.model.user.NotificationType;

import javax.persistence.*;

@Entity
public class Notification {

    public enum NotificationStatus {READY, COMPLETE}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @ManyToOne
    private Event event;

    private String userName;

    private NotificationStatus status;

    private NotificationType type;

    public Notification() {
    }

    public Notification(String userName, NotificationType type, Event event) {
        this.event = event;
        this.userName = userName;
        this.type = type;
        this.status = NotificationStatus.READY;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Notification{" +
                       "userName='" + userName + '\'' +
                       ", type=" + type +
                       ", status=" + status +
                       ", event=" + event +
                       '}';
    }
}
