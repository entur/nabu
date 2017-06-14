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

        if (filter.getOrganisation() != null) {
            // TODO need to get providerid from organisation for exact match
            if (jobEvent.getReferential() == null || !jobEvent.getReferential().endsWith(filter.getOrganisation().getPrivateCode().toLowerCase())) {
                return false;
            }

        }

        return filter.getJobDomain().equals(jobEvent.getDomain()) &&
                       filter.getStates().contains(jobEvent.getState()) &&
                       (filter.getActions().contains(JobEventFilter.ALL_TYPES) || filter.getActions().contains(jobEvent.getAction()));

    }
}
