package no.rutebanken.nabu.organisation.rest.exception;

import no.rutebanken.nabu.organisation.model.OrganisationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class OrganisationExceptionMapper implements ExceptionMapper<OrganisationException> {

	@Override
	public Response toResponse(OrganisationException e) {
		return Response
				       .status(e.getStatusCode())
				       .type(MediaType.TEXT_PLAIN)
				       .entity(e.getMessage())
				       .build();
	}
}
