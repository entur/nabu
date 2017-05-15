package no.rutebanken.nabu.jms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.rutebanken.nabu.domain.event.JobState;

import java.io.IOException;
import java.time.Instant;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobEventDTO {

    public Instant eventTime;

    public String correlationId;

    public String domain;
    public String action;
    public JobState state;


    public String externalId;

    public Long providerId;

    public String referential;

    public Long version;

    public String name;

    public static JobEventDTO fromString(String string) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(string, JobEventDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
