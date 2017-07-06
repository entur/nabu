package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check whether an Event matches a given CrudEventFilter.
 */
public class CrudEventMatcher implements EventMatcher {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private CrudEventFilter crudEventFilter;

    private static final String ENTITY_TYPE = "EntityType";

    public CrudEventMatcher(CrudEventFilter crudEventFilter) {
        this.crudEventFilter = crudEventFilter;
        // Ensure (potentially to be cached) administrative zone polygons are loaded.
        this.crudEventFilter.getAdministrativeZones().forEach(az -> az.getPolygon().isValid());
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
        return crudEventFilter.getEntityClassifications().stream().allMatch(ec -> isMatch(crudEvent, ec));
    }

    private boolean isMatch(CrudEvent crudEvent, EntityClassification ec) {
        if (isEntityTypeCriterion(ec)) {
            return ec.isMatch(crudEvent.getEntityType());
        } else if (isSubTypeCriterion(ec)) {
            return isSubTypeMatch(crudEvent, ec);
        }

        logger.warn("Unable to check entityClassification: " + ec.getId() + " for crudEventFilter. Ignored.");
        return true;
    }



    private boolean isEntityTypeCriterion(EntityClassification ec) {
        return ENTITY_TYPE.equals(ec.getEntityType().getPrivateCode());
    }


    // Verify subtype
    private boolean isSubTypeMatch(CrudEvent crudEvent, EntityClassification ec) {
        return ec.isMatch(crudEvent.getEntityClassifier()) && ec.getEntityType().getPrivateCode().replace("Type","").equals(crudEvent.getEntityType());
    }

    // TODO This is highly dubious, how can we be sure whether this is a subtype criterion?
    private boolean isSubTypeCriterion(EntityClassification ec) {
        return !isEntityTypeCriterion(ec) && ec.getEntityType().getPrivateCode().endsWith("Type");
    }

    private boolean matchesAdministrativeZone(CrudEvent crudEvent) {
        if (crudEvent.getGeometry() == null || crudEventFilter.getAdministrativeZones().isEmpty()) {
            return true;
        }
        return crudEventFilter.getAdministrativeZones().stream().anyMatch(az -> az.getPolygon().contains(crudEvent.getGeometry()));
    }
}
