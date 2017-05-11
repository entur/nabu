package no.rutebanken.nabu.jms.mapper;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.domain.SystemStatus;
import no.rutebanken.nabu.domain.event.*;
import no.rutebanken.nabu.jms.dto.EventDTO;
import org.wololo.jts2geojson.GeoJSONReader;

import java.util.Arrays;

import static no.rutebanken.nabu.jms.dto.EventDTO.EventType.CRUD;

public class EventMapper {

    public Event toEvent(EventDTO dto) {
        Event event;
        if (CRUD.equals(dto.eventType)) {
            event = toCrudEvent(dto);
        } else {
            event = toJobEvent(dto);
        }

        event.setAction(dto.action);
        event.setActionSubType(dto.actionSubType);
        event.setCorrelationId(dto.correlationId);
        event.setEventTime(dto.eventTime);
        event.setExternalId(dto.externalId);
        event.setName(dto.name);

        return event;
    }

    // Tmp conversion until marduk is updated to send Events
    public JobEvent toJobEvent(Status status) {
        JobEvent event = new JobEvent();

        event.setAction(status.action.getEventAction());

        event.setActionSubType(status.action.name());
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
        event.setAction(EventAction.valueOf(status.action.name()));
        event.setCorrelationId(status.correlationId);
        event.setEventTime(status.date.toInstant());
        if (JobEvent.JobDomain.GRAPH.equals(status.entity)) {
            event.setJobType(status.entity);
        } else {
            event.setDomain(JobEvent.JobDomain.GEOCODER);
        }

        event.setState(JobState.valueOf(status.state.name()));
        event.setActionSubType(status.jobType);
        return event;
    }


    private CrudEvent toCrudEvent(EventDTO dto) {
        CrudEvent crudEvent = new CrudEvent();

        crudEvent.setEntityType(dto.entityType);
        crudEvent.setVersion(dto.version);
        crudEvent.setEntityClassifier(dto.entityClassifier);
        crudEvent.setOldValue(dto.oldValue);
        crudEvent.setNewValue(dto.newValue);

        if (dto.geometry != null) {
            crudEvent.setGeometry(new GeoJSONReader().read(dto.geometry));
        }

        return crudEvent;
    }

    private JobEvent toJobEvent(EventDTO dto) {
        JobEvent jobEvent = new JobEvent();

        jobEvent.setProviderId(dto.providerId);
        jobEvent.setReferential(dto.referential);
        jobEvent.setState(dto.state);
        jobEvent.setJobType(dto.entityType);

        return jobEvent;
    }
}
