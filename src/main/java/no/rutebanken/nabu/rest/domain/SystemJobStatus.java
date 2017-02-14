package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonRootName("systemJobStatus")
public class SystemJobStatus {


	public enum Action {FILE_TRANSFER, EXPORT, UPDATE, BUILD_GRAPH}

	public enum State {PENDING, STARTED, TIMEOUT, FAILED, OK}

	@JsonProperty("events")
	private List<SystemJobStatusEvent> events = new ArrayList();

	@JsonProperty("action")
	private SystemJobStatus.Action action;

	@JsonProperty("correlationId")
	private String correlationId;

	@JsonProperty("firstEvent")
	private Date firstEvent;

	@JsonProperty("lastEvent")
	private Date lastEvent;

	@JsonProperty("durationMillis")
	private Long durationMillis;

	@JsonProperty("endState")
	private SystemJobStatus.State endStatus;

	@JsonProperty("entity")
	public String entity;


	public List<SystemJobStatusEvent> getEvents() {
		return events;
	}

	public void setEvents(List<SystemJobStatusEvent> events) {
		this.events = events;
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

	public Long getDurationMillis() {
		return durationMillis;
	}

	public void setDurationMillis(Long durationMillis) {
		this.durationMillis = durationMillis;
	}

	public State getEndStatus() {
		return endStatus;
	}

	public void setEndStatus(State endStatus) {
		this.endStatus = endStatus;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void addEvent(SystemJobStatusEvent event) {
		events.add(event);
	}

}
