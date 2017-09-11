package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.Event;

public interface EventMatcher {

    String ALL_TYPES = "*";

    boolean matches(Event event);
}
