package no.rutebanken.nabu.jms.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.rutebanken.nabu.domain.event.EventAction;
import no.rutebanken.nabu.domain.event.JobState;
import org.wololo.geojson.Geometry;

import java.io.IOException;
import java.time.Instant;

public class EventDTO {

    public enum EventType {JOB, CRUD}

    public Instant eventTime;

    public EventType eventType;

    public EventAction action;

    public String correlationId;

    public String entityType;

    public String entityClassifier;

    public JobState state;

    public String actionSubType;

    public Long providerId;

    public String externalId;

    public Long version;

    public String name;

    public String referential;

    public String oldValue;

    public String newValue;

    public Geometry geometry;


    public static EventDTO fromString(String string) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(string, EventDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                       "eventType=" + eventType +
                       ", eventTime=" + eventTime +
                       ", action=" + action +
                       ", actionSubType='" + actionSubType + '\'' +
                       ", state=" + state +
                       ", externalId='" + externalId + '\'' +
                       ", name='" + name + '\'' +
                       '}';
    }
}
