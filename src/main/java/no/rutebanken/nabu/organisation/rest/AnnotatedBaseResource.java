package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public abstract class AnnotatedBaseResource<E extends VersionedEntity, D extends BaseDTO> extends BaseResource<E, D> {

	@POST
	public Response create(D dto, @Context UriInfo uriInfo) {
		return super.createEntity(dto, uriInfo);
	}


	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, D dto) {
		super.updateEntity(id, dto);
	}


	@GET
	@Path("{id}")
	public D get(@PathParam("id") String id) {
		return super.getEntity(id);
	}

	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) {
		super.deleteEntity(id);
	}

	@GET
	public List<D> listAll() {
		return super.listAllEntities();
	}
}
