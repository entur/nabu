/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
    private List<JobStatusEvent> events = new ArrayList<>();

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

    @JsonProperty("providerId")
    private Long providerId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("errorCode")
    private String errorCode;

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

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
