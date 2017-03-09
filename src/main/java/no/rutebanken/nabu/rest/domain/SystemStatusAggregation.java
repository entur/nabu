package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import no.rutebanken.nabu.domain.SystemStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonRootName("systemStatusAggregation")
public class SystemStatusAggregation {

	@JsonProperty("jobType")
	public String jobType;

	@JsonProperty("entity")
	public String entity;

	@JsonProperty("source")
	public String source;

	@JsonProperty("target")
	public String target;

	@JsonProperty("currentState")
	public SystemStatus.State currentState;

	@JsonProperty("currentStateDate")
	public Date currentStateDate;

	@JsonProperty("latestDatePerState")
	public Map<SystemStatus.State, Date> latestDatePerState = new HashMap<>();


	public SystemStatusAggregation(SystemStatus in) {
		this.jobType = in.jobType;
		this.entity = in.entity;
		this.source = in.source;
		this.target = in.target;
	}

	public void addSystemStatus(SystemStatus in) {

		if (currentStateDate == null || currentStateDate.before(in.date)) {
			currentStateDate = in.date;
			currentState = in.state;
		}

		Date latestDateForState = latestDatePerState.get(in.state);
		if (latestDateForState == null || latestDateForState.before(in.date)) {
			latestDatePerState.put(in.state, in.date);
		}

	}
}
