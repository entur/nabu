package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;

public class JobEventFilterMatcher implements EventFilterMatcher {

    private JobEventFilter jobEventFilter;

    public JobEventFilterMatcher(JobEventFilter jobEventFilter) {
        this.jobEventFilter = jobEventFilter;
    }

    @Override
    public boolean matches(Event event) {
        return false;
    }
}
