package no.rutebanken.nabu.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

@Entity
@Table(name = "system_status", indexes = {@Index(name = "ss_status", columnList = "action,correlationId,date"),
		                                         @Index(name = "agg_status", columnList = "jobType,state,date")})
public class SystemStatus {

	public enum Action {FILE_TRANSFER, EXPORT, UPDATE, BUILD}

	public enum State {PENDING, STARTED, TIMEOUT, FAILED, OK}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@JsonProperty("correlation_id")
	public String correlationId;

	@JsonProperty("action")
	public SystemStatus.Action action;

	@JsonProperty("state")
	public SystemStatus.State state;

	@JsonProperty("entity")
	public String entity;

	@JsonProperty("source")
	public String source;

	@JsonProperty("jobType")
	public String jobType;

	@JsonProperty("target")
	public String target;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "CET")
	@JsonProperty("date")
	public Date date;

	public SystemStatus() {
		// Must be present for JSON unmarshalling
	}

	public SystemStatus(String jobType, String correlationId, State state, Date date) {
		this.jobType = jobType;
		this.correlationId = correlationId;
		this.state = state;
		this.date = date;
	}


	public SystemStatus(String jobType, String correlationId, Action action, State state, String entity, String source, String target, Date date) {
		this(jobType, correlationId, state, date);
		this.entity = entity;
		this.source = source;
		this.target = target;
		this.action = action;
	}

	public static SystemStatus fromString(String string) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(string, SystemStatus.class);
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

		SystemStatus that = (SystemStatus) o;

		return id != null ? id.equals(that.id) : that.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
