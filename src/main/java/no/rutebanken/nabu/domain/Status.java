package no.rutebanken.nabu.domain;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "status", indexes = { @Index(name = "i_status", columnList = "providerId,correlationId,date") })
public class Status {

	/*
	 * Example
	 * 
	 * {"file_name":"20160229_20161231_23_v3.zip","correlation_id":"4340263f-2342-4c37-8cdf-157c95499fb2",
	 * "provider_id":2,"job_id":2345,"action":"IMPORT","state":"FAILED","date":"2016-10-19T14:00:14.196"}
	 * 
	 */

	public enum Action {
		FILE_TRANSFER, IMPORT, EXPORT, VALIDATION, DATASPACE_TRANSFER
	}

	public enum State {
		PENDING, STARTED, TIMEOUT, FAILED, OK
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
	public Action action;

	@JsonProperty("state")
	public State state;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "CET")
	@JsonProperty("date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;

	public Status(String fileName, Long providerId, Long jobId, Action action, State state, String correlationId, Date date) {
		this.fileName = fileName;
		this.providerId = providerId;
		this.jobId = jobId;
		this.action = action;
		this.state = state;
		this.correlationId = correlationId;
		this.date = date; // LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
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
