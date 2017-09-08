package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.exceptions.NabuException;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.EventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;
import org.springframework.stereotype.Service;

@Service
public class EventMatcherFactory {


    public EventMatcher createEventMatcher(EventFilter eventFilter) {

        if (eventFilter instanceof CrudEventFilter) {
            return new CrudEventMatcher((CrudEventFilter) eventFilter);
        } else if (eventFilter instanceof JobEventFilter) {
            return new JobEventMatcher((JobEventFilter) eventFilter);
        }
        throw new NabuException("Unsupported eventFilter typer: " + eventFilter);
    }
}
