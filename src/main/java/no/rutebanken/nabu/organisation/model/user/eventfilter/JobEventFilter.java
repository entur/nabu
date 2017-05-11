package no.rutebanken.nabu.organisation.model.user.eventfilter;

import no.rutebanken.nabu.domain.event.EventAction;
import no.rutebanken.nabu.domain.event.JobState;

import javax.persistence.Entity;

@Entity
public class JobEventFilter extends EventFilter{

    private EventAction action;

    private JobState state;

    public EventAction getAction() {
        return action;
    }

    public void setAction(EventAction action) {
        this.action = action;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }
}
