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

package no.rutebanken.nabu.event.listener.mapper;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.event.listener.dto.CrudEventDTO;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import org.wololo.jts2geojson.GeoJSONReader;

public class EventMapper {

    public CrudEvent toCrudEvent(CrudEventDTO dto) {
        CrudEvent event = new CrudEvent();

        event.setAction(dto.action);
        event.setAction(dto.action);
        event.setCorrelationId(dto.correlationId);
        event.setEventTime(dto.eventTime);
        event.setExternalId(dto.externalId);
        event.setName(dto.name);
        event.setChangeType(dto.changeType);

        event.setEntityType(dto.entityType);
        event.setVersion(dto.version);
        event.setEntityClassifier(dto.entityClassifier);
        event.setOldValue(dto.oldValue);
        event.setNewValue(dto.newValue);
        event.setComment(dto.comment);
        event.setUsername(dto.username);
        event.setErrorCode(dto.errorCode);
        event.setLocation(dto.location);

        if (dto.geometry != null) {
            event.setGeometry(new GeoJSONReader().read(dto.geometry));
        }

        return event;
    }

    public JobEvent toJobEvent(JobEventDTO dto) {
        JobEvent event = new JobEvent();
        event.setAction(dto.getAction());
        event.setCorrelationId(dto.getCorrelationId());
        event.setEventTime(dto.getEventTime());
        event.setExternalId(dto.getExternalId());
        event.setName(dto.getName());
        event.setProviderId(dto.getProviderId());
        event.setReferential(dto.getReferential());
        event.setState(dto.getState());
        event.setDomain(dto.getDomain());
        event.setUsername(dto.getUsername());
        event.setErrorCode(dto.getErrorCode());

        return event;
    }
}
