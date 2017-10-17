package no.rutebanken.nabu.jms.mapper;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.jms.dto.CrudEventDTO;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
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
        event.setLocation(dto.location);

        if (dto.geometry != null) {
            event.setGeometry(new GeoJSONReader().read(dto.geometry));
        }

        return event;
    }

    public JobEvent toJobEvent(JobEventDTO dto) {
        JobEvent event = new JobEvent();
        event.setAction(dto.action);
        event.setCorrelationId(dto.correlationId);
        event.setEventTime(dto.eventTime);
        event.setExternalId(dto.externalId);
        event.setName(dto.name);
        event.setProviderId(dto.providerId);
        event.setReferential(dto.referential);
        event.setState(dto.state);
        event.setDomain(dto.domain);

        return event;
    }
}
