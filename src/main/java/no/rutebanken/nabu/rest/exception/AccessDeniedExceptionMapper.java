package no.rutebanken.nabu.rest.exception;

import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException e) {
        return Response
                       .status(Response.Status.FORBIDDEN)
                       .type(MediaType.TEXT_PLAIN)
                       .entity(e.getMessage())
                       .build();
    }
}
