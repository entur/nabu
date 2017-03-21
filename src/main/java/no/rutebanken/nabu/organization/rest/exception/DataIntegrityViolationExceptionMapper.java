package no.rutebanken.nabu.organization.rest.exception;

import org.springframework.dao.DataIntegrityViolationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class DataIntegrityViolationExceptionMapper implements ExceptionMapper<DataIntegrityViolationException> {

	@Override
	public Response toResponse(DataIntegrityViolationException e) {
		Throwable t = e;
		if (e.getRootCause() != null) {
			t = e.getRootCause();
		}

		return Response
				       .status(Response.Status.BAD_REQUEST.getStatusCode())
				       .type(MediaType.TEXT_PLAIN)
				       .entity(t.getMessage())
				       .build();
	}
}
