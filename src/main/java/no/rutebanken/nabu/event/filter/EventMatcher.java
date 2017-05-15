package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.Event;

public interface EventMatcher {

    boolean matches(Event event);
}
