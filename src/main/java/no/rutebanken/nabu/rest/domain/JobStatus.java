package no.rutebanken.nabu.rest.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("jobs")
public class JobStatus {

    public enum State {
        PENDING, STARTED, TIMEOUT, FAILED, OK, DUPLICATE, CANCELLED
    }

    @JsonProperty("events")
    private List<JobStatusEvent> events = new ArrayList<JobStatusEvent>();

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("firstEvent")
    private Date firstEvent;

    @JsonProperty("lastEvent")
    private Date lastEvent;

    @JsonProperty("durationMillis")
    private Long durationMillis;

    @JsonProperty("endState")
    private State endStatus;

    @JsonProperty("fileName")
    private String fileName;

    public List<JobStatusEvent> getEvents() {
        return events;
    }

    public void addEvent(JobStatusEvent event) {
        events.add(event);
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Date getFirstEvent() {
        return firstEvent;
    }

    public void setFirstEvent(Date firstEvent) {
        this.firstEvent = firstEvent;
    }

    public Date getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(Date lastEvent) {
        this.lastEvent = lastEvent;
    }

    public State getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(State endStatus) {
        this.endStatus = endStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }

}
