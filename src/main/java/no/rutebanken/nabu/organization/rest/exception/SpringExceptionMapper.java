package no.rutebanken.nabu.organization.rest.exception;

import com.google.common.collect.Sets;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpringExceptionMapper implements ExceptionMapper<NestedRuntimeException> {

	private Map<Response.Status, Set<Class>> mapping;

	public SpringExceptionMapper() {
		mapping = new HashMap<>();
		mapping.put(Response.Status.BAD_REQUEST,
				Sets.newHashSet(OptimisticLockException.class, EntityNotFoundException.class, DataIntegrityViolationException.class));
		mapping.put(Response.Status.CONFLICT, Sets.newHashSet(EntityExistsException.class));
	}


	@Override
	public Response toResponse(NestedRuntimeException e) {
		Throwable t = e;
		if (e.getRootCause() != null) {
			t = e.getRootCause();
		}

		return Response
				       .status(toStatus(t))
				       .type(MediaType.TEXT_PLAIN)
				       .entity(t.getMessage())
				       .build();
	}


	private int toStatus(Throwable e) {
		for (Map.Entry<Response.Status, Set<Class>> entry : mapping.entrySet()) {
			if (entry.getValue().stream().anyMatch(c -> c.isAssignableFrom(e.getClass()))) {
				return entry.getKey().getStatusCode();
			}
		}
		return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	}
}
