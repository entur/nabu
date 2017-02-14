package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.rutebanken.nabu.domain.SystemStatus;

import java.util.Date;

public class SystemJobStatusEvent {

	@JsonProperty("state")
	public SystemJobStatus.State state;

	@JsonProperty("date")
	public Date date;


	public SystemJobStatusEvent(SystemJobStatus.State state, Date date) {
		this.state = state;
		this.date = date;
	}

	public static SystemJobStatusEvent createFromSystemStatus(SystemStatus e) {
		return new SystemJobStatusEvent(SystemJobStatus.State.valueOf(e.state.name()), e.date);
	}
}
