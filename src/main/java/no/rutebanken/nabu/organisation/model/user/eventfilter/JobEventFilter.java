package no.rutebanken.nabu.organisation.model.user.eventfilter;

import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.filter.EventMatcher;
import no.rutebanken.nabu.event.filter.JobEventMatcher;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * User defined event filter for job related events.
 */
@Entity
public class JobEventFilter extends EventFilter {

    @NotNull
    private String jobDomain;

    @NotNull
    private String action;

    @NotNull
    private JobState state;

    public String getJobDomain() {
        return jobDomain;
    }

    public void setJobDomain(String jobDomain) {
        this.jobDomain = jobDomain;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    @Override
    public EventMatcher getMatcher() {
        return new JobEventMatcher(this);
    }
}
