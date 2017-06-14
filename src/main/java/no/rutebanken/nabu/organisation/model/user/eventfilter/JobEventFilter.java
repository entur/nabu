package no.rutebanken.nabu.organisation.model.user.eventfilter;

import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.filter.EventMatcher;
import no.rutebanken.nabu.event.filter.JobEventMatcher;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * User defined event filter for job related events.
 */
@Entity
public class JobEventFilter extends EventFilter {

    public static final String ALL_TYPES = "*";

    @NotNull
    private String jobDomain;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> actions;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<JobState> states;

    public String getJobDomain() {
        return jobDomain;
    }

    public void setJobDomain(String jobDomain) {
        this.jobDomain = jobDomain;
    }

    public Set<String> getActions() {
        if (actions == null) {
            actions = new HashSet<>();
        }
        return actions;
    }

    public void setActions(Set<String> actions) {
        getActions().clear();
        getActions().addAll(actions);
    }

    public Set<JobState> getStates() {
        if (states == null) {
            states = new HashSet<>();
        }
        return states;
    }

    public void setStates(Set<JobState> states) {
        getStates().clear();
        getStates().addAll(states);
    }

    @Override
    public EventMatcher getMatcher() {
        return new JobEventMatcher(this);
    }

}
