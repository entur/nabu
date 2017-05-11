package no.rutebanken.nabu.domain.event;

import no.rutebanken.nabu.domain.Status;
import org.apache.commons.lang.ObjectUtils;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(indexes = {@Index(name = "i_event_provider", columnList = "providerId,correlationId,action,eventTime")})
public abstract class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    private Instant registeredTime;

    private Instant eventTime;

    private EventAction action;

    private String actionSubType;

    private String correlationId;

    private String name;

    private String externalId;

    public Event() {
        registeredTime = Instant.now();
    }

    public Instant getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(Instant registeredTime) {
        this.registeredTime = registeredTime;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public EventAction getAction() {
        return action;
    }

    public void setAction(EventAction action) {
        this.action = action;
    }

    public String getActionSubType() {
        return actionSubType;
    }

    public void setActionSubType(String actionSubType) {
        this.actionSubType = actionSubType;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public int compareTo(Event o) {
        int corrCmp = ObjectUtils.compare(correlationId, o.correlationId);
        if (corrCmp != 0) {
            return corrCmp;
        }

        int dateCmp = ObjectUtils.compare(eventTime, o.eventTime);
        if (dateCmp != 0) {
            return dateCmp;
        }
        int actionCmp = ObjectUtils.compare(action, o.action);
        if (actionCmp != 0) {
            return actionCmp;
        }

        return ObjectUtils.compare(pk, o.pk);
    }
}
