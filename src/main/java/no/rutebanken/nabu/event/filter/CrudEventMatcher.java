/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.user.AdministrativeZoneRepository;
import no.rutebanken.nabu.event.user.dto.responsibility.EntityClassificationDTO;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check whether an Event matches a given EventFilterDTO.
 */
public class CrudEventMatcher implements EventMatcher {
    private static final String ENTITY_TYPE = "EntityType";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventFilterDTO eventFilter;
    private final AdministrativeZoneRepository administrativeZoneRepository;

    public CrudEventMatcher(AdministrativeZoneRepository administrativeZoneRepository, EventFilterDTO crudEventFilter) {
        this.eventFilter = crudEventFilter;
        this.administrativeZoneRepository = administrativeZoneRepository;
    }

    @Override
    public boolean matches(Event event) {
        if (!(event instanceof CrudEvent crudEvent)) {
            return false;
        }

        return matchesEventClassifier(crudEvent) && matchesAdministrativeZone(crudEvent);
    }

    private boolean matchesEventClassifier(CrudEvent crudEvent) {
        return eventFilter.getEntityClassifications().stream().allMatch(ec -> isMatch(crudEvent, ec));
    }

    private boolean isMatch(CrudEvent crudEvent, EntityClassificationDTO ec) {
        if (isEntityTypeCriterion(ec)) {
            return isClassificationMatch(ec, crudEvent.getEntityType());
        } else if (isSubTypeCriterion(ec)) {
            return isSubTypeMatch(crudEvent, ec);
        }

        logger.warn("Unable to check entityClassification: {} for eventFilter. Ignored.", ec);
        return true;
    }


    private boolean isEntityTypeCriterion(EntityClassificationDTO ec) {
        return ENTITY_TYPE.equals(ec.getEntityType().getPrivateCode());
    }


    // Verify subtype
    private boolean isSubTypeMatch(CrudEvent crudEvent, EntityClassificationDTO ec) {
        return isClassificationMatch(ec, crudEvent.getEntityClassifier()) && ec.getEntityType().getPrivateCode().replace("Type", "").equals(crudEvent.getEntityType());
    }

    // TODO This is highly dubious, how can we be sure whether this is a subtype criterion?
    private boolean isSubTypeCriterion(EntityClassificationDTO ec) {
        return !isEntityTypeCriterion(ec) && ec.getEntityType().getPrivateCode().endsWith("Type");
    }

    private boolean matchesAdministrativeZone(CrudEvent crudEvent) {
        if (crudEvent.getGeometry() == null || eventFilter.getAdministrativeZoneRefs().isEmpty()) {
            return true;
        }
        return eventFilter.getAdministrativeZoneRefs().stream().map(administrativeZoneRepository::getAdministrativeZone)
                       .anyMatch(az -> az.getPolygon().contains(crudEvent.getGeometry()));
    }

    private boolean isClassificationMatch(EntityClassificationDTO entityClassification, String entityClassificationCode) {
        if (EventMatcher.ALL_TYPES.equals(entityClassification.getPrivateCode())) {
            return true;
        } else return entityClassification.getPrivateCode().equals(entityClassificationCode);
    }

}
