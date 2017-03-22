package no.rutebanken.nabu.organization.rest.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SpringExceptionMapper extends ExceptionMapperBase implements ExceptionMapper<NestedRuntimeException> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Override
	public Response toResponse(NestedRuntimeException e) {
		logger.debug("Operation failed with exception: " + e.getMessage(), e);
		Throwable t = e;
		if (e.getRootCause() != null) {
			t = e.getRootCause();
		}

		return super.buildResponse(t);
	}


}
