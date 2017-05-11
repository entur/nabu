package no.rutebanken.nabu.organisation.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.rutebanken.nabu.organisation.model.user.eventfilter.EventFilter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
public class NotificationConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long pk;

    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationType notificationType;


    @ManyToOne
    private EventFilter eventFilter;

    @NotNull
    private Instant notifyFrom;

    public NotificationConfiguration() {
        notifyFrom = Instant.now();
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public EventFilter getEventFilter() {
        return eventFilter;
    }

    public void setEventFilter(EventFilter eventFilter) {
        this.eventFilter = eventFilter;
    }

    public Instant getNotifyFrom() {
        return notifyFrom;
    }

    public void setNotifyFrom(Instant notifyFrom) {
        this.notifyFrom = notifyFrom;
    }
}
