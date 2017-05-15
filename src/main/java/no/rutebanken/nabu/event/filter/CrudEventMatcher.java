package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;

/**
 * Check whether an Event matches a given CrudEventFilter.
 */
public class CrudEventMatcher implements EventMatcher {

    private CrudEventFilter crudEventFilter;

    public CrudEventMatcher(CrudEventFilter crudEventFilter) {
        this.crudEventFilter = crudEventFilter;
    }

    @Override
    public boolean matches(Event event) {
        if (!(event instanceof CrudEvent)) {
            return false;
        }
        CrudEvent crudEvent = (CrudEvent) event;

        return matchesEventClassifier(crudEvent) && matchesAdministrativeZone(crudEvent);
    }

    private boolean matchesEventClassifier(CrudEvent crudEvent) {
        return crudEventFilter.getEntityClassifications().stream().anyMatch(ec -> isMatch(crudEvent, ec));
    }

    private boolean isMatch(CrudEvent crudEvent, EntityClassification ec) {
        return ec.getEntityType().getPrivateCode().equals(crudEvent.getEntityType()) && ec.isMatch(crudEvent.getEntityClassifier());
    }

    private boolean matchesAdministrativeZone(CrudEvent crudEvent) {
        if (crudEvent.getGeometry() == null || crudEventFilter.getAdministrativeZones().isEmpty()) {
            return true;
        }
        return crudEventFilter.getAdministrativeZones().stream().anyMatch(az -> az.getPolygon().contains(crudEvent.getGeometry()));
    }
}
