package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.event.user.AdministrativeZoneRepository;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import no.rutebanken.nabu.exceptions.NabuException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventMatcherFactory {

    @Autowired
    private AdministrativeZoneRepository administrativeZoneRepository;

    public EventMatcher createEventMatcher(EventFilterDTO eventFilter) {

        if (EventFilterDTO.EventFilterType.CRUD.equals(eventFilter.type)) {
            return new CrudEventMatcher(administrativeZoneRepository, eventFilter);
        } else if (EventFilterDTO.EventFilterType.JOB.equals(eventFilter.type)) {
            return new JobEventMatcher(eventFilter);
        }
        throw new NabuException("Unsupported eventFilter type: " + eventFilter.type);
    }
}
