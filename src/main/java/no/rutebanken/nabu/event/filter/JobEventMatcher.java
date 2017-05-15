package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;

/**
 * Check whether an Event matches a given JobEventFilter.
 */
public class JobEventMatcher implements EventMatcher {

    private JobEventFilter filter;

    public JobEventMatcher(JobEventFilter jobEventFilter) {
        this.filter = jobEventFilter;
    }

    @Override
    public boolean matches(Event event) {
        if (!(event instanceof JobEvent)) {
            return false;
        }
        JobEvent jobEvent = (JobEvent) event;

        // TODO match organisation against provider id

        return filter.getJobDomain().equals(jobEvent.getDomain()) &&
                       filter.getState().equals(jobEvent.getState()) &&
                       filter.getAction().equals(jobEvent.getAction());


    }
}
