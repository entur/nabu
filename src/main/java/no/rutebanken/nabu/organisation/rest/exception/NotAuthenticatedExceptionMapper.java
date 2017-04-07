package no.rutebanken.nabu.organisation.rest.exception;

import org.rutebanken.helper.organisation.NotAuthenticatedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAuthenticatedExceptionMapper implements ExceptionMapper<NotAuthenticatedException> {

    @Override
    public Response toResponse(NotAuthenticatedException e) {
        return Response
                       .status(Response.Status.UNAUTHORIZED)
                       .type(MediaType.TEXT_PLAIN)
                       .entity(e.getMessage())
                       .build();
    }
}
