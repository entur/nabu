package no.rutebanken.nabu.domain.event;

import org.apache.commons.lang.ObjectUtils;
import org.wololo.geojson.Geometry;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(indexes = {@Index(name = "i_event_provider", columnList = "providerId,correlationId,action,eventTime")})
public abstract class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @NotNull
    private Instant registeredTime;
    @NotNull
    private Instant eventTime;
    @NotNull
    private String action;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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


    public abstract static class EventBuilder<T extends Event> {

        protected T event;

        protected EventBuilder(T event) {
            this.event = event;
        }


        public EventBuilder<T>  action(Enum action) {
            return action(action.name());
        }

        public EventBuilder<T> action(String action) {
            event.setAction(action);
            return this;
        }

        public EventBuilder<T> eventTime(Instant time) {
            event.setEventTime(time);
            return this;
        }

        public EventBuilder<T> name(String name) {
            event.setName(name);
            return this;
        }

        public EventBuilder<T> externalId(String externalId) {
            event.setExternalId(externalId);
            return this;
        }


        public EventBuilder<T> correlationId(String correlationId) {
            event.setCorrelationId(correlationId);
            return this;
        }

        public T build() {
            return event;
        }
    }


}
