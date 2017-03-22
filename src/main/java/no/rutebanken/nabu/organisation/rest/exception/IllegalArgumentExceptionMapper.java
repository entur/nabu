package no.rutebanken.nabu.organisation.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

	@Override
	public Response toResponse(IllegalArgumentException e) {
		return Response
				       .status(Response.Status.BAD_REQUEST)
				       .type(MediaType.TEXT_PLAIN)
				       .entity(e.getMessage())
				       .build();
	}
}
