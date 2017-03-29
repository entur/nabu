package no.rutebanken.nabu.organisation.model;

import org.springframework.http.HttpStatus;

public class OrganisationException extends RuntimeException {

	private int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

	public OrganisationException() {
		super();
	}

	public OrganisationException(String message) {
		super(message);
	}

	public OrganisationException(int statusCode) {
		this.statusCode = statusCode;
	}

	public OrganisationException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
