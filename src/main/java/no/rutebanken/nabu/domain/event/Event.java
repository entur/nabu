/*
 *
 *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *
 */

package no.rutebanken.nabu.domain.event;

import org.apache.commons.lang.ObjectUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

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

    private String username;

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

    @Override
    public int compareTo(Event o) {
        int corrCmp = ObjectUtils.compare(correlationId, o.correlationId);
        if (corrCmp != 0) {
            return corrCmp;
        }
        int extermalIdCmp = ObjectUtils.compare(externalId, o.externalId);
        if (extermalIdCmp != 0) {
            return extermalIdCmp;
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

        public EventBuilder<T> username(String username) {
            event.setUsername(username);
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
