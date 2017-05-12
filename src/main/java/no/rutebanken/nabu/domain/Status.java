package no.rutebanken.nabu.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import org.apache.commons.lang.ObjectUtils;

import javax.persistence.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

@Entity
@Table(name = "status", indexes = {@Index(name = "i_status", columnList = "providerId,correlationId,date")})
public class Status implements Comparable<Status> {

    public enum State {
        PENDING, STARTED, TIMEOUT, FAILED, OK, DUPLICATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

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
    @Temporal(TemporalType.TIMESTAMP)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        return id != null ? id.equals(status.id) : status.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(Status o) {
        int corrCmp = ObjectUtils.compare(correlationId, o.correlationId);
        if (corrCmp != 0) {
            return corrCmp;
        }

        int dateCmp = ObjectUtils.compare(date, o.date);
        if (dateCmp != 0) {
            return dateCmp;
        }
        int actionCmp = ObjectUtils.compare(action, o.action);
        if (actionCmp != 0) {
            return actionCmp;
        }
        int stateCmp = ObjectUtils.compare(state, o.state);
        if (stateCmp != 0) {
            return stateCmp;
        }

        return ObjectUtils.compare(id, o.id);
    }
}
