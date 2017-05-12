package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Event;

public interface EventHandler {

    void onEvent(Event event);
}
