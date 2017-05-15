package no.rutebanken.nabu.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.domain.event.TimeTableAction;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

// TODO remove when Marduk switches to JobEvent
public class Status  {

    public enum State {
        PENDING, STARTED, TIMEOUT, FAILED, OK, DUPLICATE
    }

    @JsonProperty("correlation_id")
    public String correlationId;

    @JsonProperty("file_name")
    public String fileName;

    @JsonProperty("provider_id")
    public Long providerId;

    @JsonProperty("job_id")
    public Long jobId;

    @JsonProperty("action")
    public TimeTableAction action;

    @JsonProperty("state")
    public State state;

    @JsonProperty("referential")
    public String referential;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "CET")
    @JsonProperty("date")
    public Date date;

    public Status(String fileName, Long providerId, Long jobId, TimeTableAction action, State state, String correlationId,
                         Date date, String referential) {
        this.fileName = fileName;
        this.providerId = providerId;
        this.jobId = jobId;
        this.action = action;
        this.state = state;
        this.correlationId = correlationId;
        this.date = date; // LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        this.referential = referential;
    }

    public Status() {
        // Must be present for JSON unmarshalling
    }

    public static Status fromString(String string) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(string, Status.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
