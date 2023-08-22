/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.domain.event;

import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(indexes = {@Index(name = "i_event_provider", columnList = "providerId,correlationId,action,eventTime")})
public abstract class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "event_seq")
    private Long pk;

    @NotNull(message = "registeredTime cannot be null")
    private final Instant registeredTime;
    @NotNull(message = "eventTime cannot be null")
    private Instant eventTime;
    @NotNull(message = "action cannot be null")
    @Size(max = 255, message = "action cannot be longer than 255 characters")
    private String action;

    @Size(max = 255, message = "correlationId cannot be longer than 255 characters")
    private String correlationId;

    @Size(max = 255, message = "name cannot be longer than 255 characters")
    private String name;

    @Size(max = 255, message = "externalId cannot be longer than 255 characters")
    private String externalId;

    @Size(max = 255, message = "username cannot be longer than 255 characters")
    private String username;

    @Size(max = 255, message = "errorCode cannot be longer than 255 characters")
    private String errorCode;

    protected Event() {
        registeredTime = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public Instant getRegisteredTime() {
        return registeredTime;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = Objects.requireNonNull(eventTime).truncatedTo(ChronoUnit.MICROS);
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

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public int compareTo(Event o) {
        int corrCmp = ObjectUtils.compare(correlationId, o.correlationId);
        if (corrCmp != 0) {
            return corrCmp;
        }
        int externalIdCmp = ObjectUtils.compare(externalId, o.externalId);
        if (externalIdCmp != 0) {
            return externalIdCmp;
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

        protected final T event;

        protected EventBuilder(T event) {
            this.event = event;
        }


        public EventBuilder<T>  action(EventAction action) {
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

        public EventBuilder<T> username(String username) {
            event.setUsername(username);
            return this;
        }

        public EventBuilder<T> errorCode(String errorCode) {
            event.setErrorCode(errorCode);
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

    @Override
    public String toString() {
        return "Event{" +
                       "eventTime=" + eventTime +
                       ", action='" + action + '\'' +
                       ", correlationId='" + correlationId + '\'' +
                       ", name='" + name + '\'' +
                       ", externalId='" + externalId + '\'' +
                       '}';
    }
}
