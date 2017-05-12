package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;

public class CrudEventFilterMatcher implements EventFilterMatcher {

    private CrudEventFilter crudEventFilter;

    public CrudEventFilterMatcher(CrudEventFilter crudEventFilter) {
        this.crudEventFilter = crudEventFilter;
    }

    @Override
    public boolean matches(Event event) {
        return false;
    }
}
