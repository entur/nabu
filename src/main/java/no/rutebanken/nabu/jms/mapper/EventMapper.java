package no.rutebanken.nabu.jms.mapper;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.domain.SystemStatus;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.jms.dto.CrudEventDTO;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
import org.wololo.jts2geojson.GeoJSONReader;

public class EventMapper {


    // Tmp conversion until marduk is updated to send Events
    public JobEvent toJobEvent(Status status) {
        JobEvent event = new JobEvent();

        event.setAction(status.action.toString());

        event.setAction(status.action.name());
        event.setCorrelationId(status.correlationId);
        event.setEventTime(status.date.toInstant());
        event.setExternalId(status.jobId == null ? null : "" + status.jobId);
        event.setName(status.fileName);
        event.setDomain(JobEvent.JobDomain.TIMETABLE);

        event.setReferential(status.referential);
        event.setProviderId(status.providerId);
        event.setState(JobState.valueOf(status.state.name()));

        return event;
    }

    // Tmp conversion until marduk is updated to send Events
    public JobEvent toJobEvent(SystemStatus status) {
        JobEvent event = new JobEvent();
        event.setAction(status.action.name());
        event.setCorrelationId(status.correlationId);
        event.setEventTime(status.date.toInstant());
        if (JobEvent.JobDomain.GRAPH.equals(status.entity)) {
            event.setDomain(status.entity);
        } else {
            event.setDomain(JobEvent.JobDomain.GEOCODER);
        }

        event.setState(JobState.valueOf(status.state.name()));
        event.setAction(status.jobType);
        return event;
    }


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
