package no.rutebanken.nabu.organization.rest.exception;

import org.springframework.transaction.TransactionSystemException;

import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TransactionSystemExceptionMapper implements ExceptionMapper<TransactionSystemException> {

	@Override
	public Response toResponse(TransactionSystemException e) {
		if (e.getRootCause() instanceof ValidationException) {
			return Response
					       .status(Response.Status.BAD_REQUEST.getStatusCode())
					       .type(MediaType.TEXT_PLAIN)
					       .entity(e.getRootCause().getMessage())
					       .build();
		}

		return Response
				       .status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
				       .type(MediaType.TEXT_PLAIN)
				       .entity(e.getMessage())
				       .build();
	}


}
