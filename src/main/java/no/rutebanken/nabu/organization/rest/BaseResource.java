package no.rutebanken.nabu.organization.rest;

import no.rutebanken.nabu.organization.model.VersionedEntity;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.BaseDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public abstract class BaseResource<E extends VersionedEntity, D extends BaseDTO> {

	protected abstract VersionedEntityRepository<E> getRepository();

	protected abstract DTOMapper<E, D> getMapper();

	protected abstract Class<E> getEntityClass();

	@POST
	public Response create(D dto, @Context UriInfo uriInfo) {
		E entity = getRepository().save(getMapper().createFromDTO(dto, getEntityClass()));

		return buildCreatedResponse(uriInfo, entity);
	}


	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, D dto) {
		E entity = getExisting(id);
		getRepository().save(getMapper().updateFromDTO(dto, entity));
	}


	@GET
	@Path("{id}")
	public D get(@PathParam("id") String id) {
		E entity = getExisting(id);
		return getMapper().toDTO(entity, true);
	}

	@GET
	public List<D> listAll() {
		return getRepository().findAll().stream().map(r -> getMapper().toDTO(r, false)).collect(Collectors.toList());
	}


	protected Response buildCreatedResponse(UriInfo uriInfo, E entity) {
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		builder.path(entity.getId());
		return Response.created(builder.build()).build();
	}

	protected E getExisting(String id) {
		try {
			return getRepository().getOneByPublicId(id);
		} catch (DataRetrievalFailureException e) {
			throw new NotFoundException(getClass().getSimpleName() + " with id: [" + id + "] not found");
		}
	}
}
