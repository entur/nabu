package no.rutebanken.nabu.organisation.rest.exception;

import com.google.common.collect.Sets;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ExceptionMapperBase {
	private Map<Response.Status, Set<Class<?>>> mapping;

	public ExceptionMapperBase() {
		mapping = new HashMap<>();
		mapping.put(Response.Status.BAD_REQUEST,
				Sets.newHashSet(ValidationException.class, OptimisticLockException.class, EntityNotFoundException.class, DataIntegrityViolationException.class));
		mapping.put(Response.Status.CONFLICT, Sets.newHashSet(EntityExistsException.class));
	}

	protected Response buildResponse(Throwable t) {
		return Response
				       .status(toStatus(t))
				       .type(MediaType.TEXT_PLAIN)
				       .entity(t.getMessage())
				       .build();
	}

	protected int toStatus(Throwable e) {
		for (Map.Entry<Response.Status, Set<Class<?>>> entry : mapping.entrySet()) {
			if (entry.getValue().stream().anyMatch(c -> c.isAssignableFrom(e.getClass()))) {
				return entry.getKey().getStatusCode();
			}
		}
		return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	}
}
