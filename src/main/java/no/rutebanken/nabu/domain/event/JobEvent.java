package no.rutebanken.nabu.domain.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;

@Entity
@DiscriminatorValue("job")
public class JobEvent extends Event {

    public enum JobDomain {TIMETABLE, GEOCODER, GRAPH}

    private JobState state;

    private Long providerId;

    private String referential;

    private String domain;

    public JobEvent() {
    }

    public JobEvent(String jobDomain, String name, Long providerId, String externalId, String actionSubType, JobState state, String correlationId, Instant eventTime, String referential) {
        this.setJobType(jobDomain);
        this.setReferential(referential);
        this.setEventTime(eventTime);
        this.setCorrelationId(correlationId);
        this.setState(state);
        this.setActionSubType(actionSubType);
        this.setProviderId(providerId);
        this.setName(name);
        this.setExternalId(externalId);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(JobDomain jobDomain) {
        this.domain = jobDomain == null ? null : jobDomain.name();
    }

    public void setJobType(String jobType) {
        this.domain = jobType;
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


}
