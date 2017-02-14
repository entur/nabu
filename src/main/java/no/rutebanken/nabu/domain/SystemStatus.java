package no.rutebanken.nabu.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

@Entity
@Table(name = "system_status", indexes = {@Index(name = "i_status", columnList = "action,correlationId,date")})
public class SystemStatus {

	public enum Action {FILE_TRANSFER, EXPORT, UPDATE, BUILD_GRAPH}

	public enum State {PENDING, STARTED, TIMEOUT, FAILED, OK}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@JsonProperty("correlation_id")
	private String correlationId;

	@JsonProperty("action")
	private SystemStatus.Action action;

	@JsonProperty("state")
	private SystemStatus.State state;

	@JsonProperty("entity")
	private String entity;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "CET")
	@JsonProperty("date")
	private Date date;

	public SystemStatus() {
		// Must be present for JSON unmarshalling
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

}
