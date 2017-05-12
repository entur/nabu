package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.Event;

public interface EventFilterMatcher {

    boolean matches(Event event);
}
