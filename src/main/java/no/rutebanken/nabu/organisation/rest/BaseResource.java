package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
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

	protected abstract DTOValidator<E, D> getValidator();


	public E createEntity(D dto) {
		getValidator().validateCreate(dto);
		E entity = getMapper().createFromDTO(dto, getEntityClass());
		if (getRepository().getOneByPublicIdIfExists(entity.getId()) != null) {
			throw new ClientErrorException(Response.Status.CONFLICT);
		}
		return getRepository().save(entity);
	}

	public Response createEntity(D dto, UriInfo uriInfo) {
		return buildCreatedResponse(uriInfo, createEntity(dto));
	}

	public E updateEntity(String id, D dto) {
		E entity = getExisting(id);
		getValidator().validateUpdate(dto, entity);
		return getRepository().save(getMapper().updateFromDTO(dto, entity));
	}


	public D getEntity(String id) {
		E entity = getExisting(id);
		return getMapper().toDTO(entity, true);
	}


	public E deleteEntity(String id) {
		E entity = getExisting(id);
		getRepository().delete(entity);
		return entity;
	}

	public List<D> listAllEntities() {
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
			throw new NotFoundException(getEntityClass().getSimpleName() + " with id: [" + id + "] not found");
		}
	}
}
