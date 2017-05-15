package no.rutebanken.nabu.jms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.rutebanken.nabu.domain.event.JobState;
import org.wololo.geojson.Geometry;

import java.io.IOException;
import java.time.Instant;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrudEventDTO {

    public Instant eventTime;

    public String correlationId;

    public String entityType;

    public String entityClassifier;

    public JobState state;

    public String action;

    public String externalId;

    public Long version;

    public String name;

    public String changeType;

    public String oldValue;

    public String newValue;

    public Geometry geometry;


    public static CrudEventDTO fromString(String string) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(string, CrudEventDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
