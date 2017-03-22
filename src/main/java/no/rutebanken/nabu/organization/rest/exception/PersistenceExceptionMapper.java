package no.rutebanken.nabu.organization.rest.exception;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PersistenceExceptionMapper extends ExceptionMapperBase implements ExceptionMapper<PersistenceException> {

	@Override
	public Response toResponse(PersistenceException e) {
		return super.buildResponse(e);
	}
}
