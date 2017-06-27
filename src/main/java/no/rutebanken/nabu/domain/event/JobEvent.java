package no.rutebanken.nabu.domain.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Events resulting from asynchronous jobs, either on behalf of a user or autonomous system jobs.
 */
@Entity
@DiscriminatorValue("job")
public class JobEvent extends Event {

    public enum JobDomain {TIMETABLE, GEOCODER, GRAPH, TIAMAT}

    @NotNull
    private JobState state;

    @NotNull
    private String domain;

    private Long providerId;

    private String referential;


    public JobEvent() {
    }

    public JobEvent(String jobDomain, String name, Long providerId, String externalId, String action, JobState state, String correlationId, Instant eventTime, String referential) {
        this.setDomain(jobDomain);
        this.setReferential(referential);
        this.setEventTime(eventTime);
        this.setCorrelationId(correlationId);
        this.setState(state);
        this.setAction(action);
        this.setProviderId(providerId);
        this.setName(name);
        this.setExternalId(externalId);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(JobDomain domain) {
        this.domain = domain == null ? null : domain.name();
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getReferential() {
        return referential;
    }

    public void setReferential(String referential) {
        this.referential = referential;
    }


    public static JobEventBuilder builder() {
        return new JobEventBuilder();
    }






    public static class JobEventBuilder extends EventBuilder<JobEvent> {

        public JobEventBuilder() {
            super(new JobEvent());
        }

        public JobEventBuilder domain(JobDomain domain) {
            event.domain = domain.name();
            return this;
        }

        public JobEventBuilder domain(String domain) {
            event.domain = domain;
            return this;
        }

        public JobEventBuilder referential(String referential) {
            event.referential = referential;
            return this;
        }

        public JobEventBuilder providerId(Long providerId) {
            event.providerId = providerId;
            return this;
        }

        public JobEventBuilder state(JobState state) {
            event.state = state;
            return this;
        }

    }

    @Override
    public String toString() {
        return "JobEvent{" +
                       "domain='" + domain + '\'' +
                       ", action='" + getAction() + '\'' +
                       ", state=" + state +'\'' +
                       ", providerId=" + providerId +'\'' +
                       ", referential='" + referential + '\'' +
                       ", eventTime=" + getEventTime() +
                       ", correlationId='" + getCorrelationId() + '\'' +
                       ", name='" + getName() + '\'' +
                       ", externalId='" + getExternalId() + '\'' +
                       '}';
    }
}
