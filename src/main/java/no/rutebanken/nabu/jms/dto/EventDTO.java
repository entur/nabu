package no.rutebanken.nabu.jms.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.rutebanken.nabu.domain.event.JobState;
import org.wololo.geojson.Geometry;

import java.io.IOException;
import java.time.Instant;

public class EventDTO {

    public enum EventType {JOB, CRUD}

    public Instant eventTime;

    public EventType eventType;

    public String correlationId;

    public String entityType;

    public String entityClassifier;

    public JobState state;

    public String action;

    public Long providerId;

    public String externalId;

    public Long version;

    public String name;

    public String referential;

    public String changeType;

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

}
