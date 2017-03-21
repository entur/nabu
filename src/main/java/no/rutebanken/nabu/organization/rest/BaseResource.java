package no.rutebanken.nabu.organization.rest;

import no.rutebanken.nabu.organization.model.VersionedEntity;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.BaseDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;
@Transactional
public abstract class BaseResource<E extends VersionedEntity, D extends BaseDTO> {

	protected abstract VersionedEntityRepository<E> getRepository();

	protected abstract DTOMapper<E, D> getMapper();

	protected abstract Class<E> getEntityClass();

	@POST
	public void create(D dto) {
		getRepository().save(getMapper().createFromDTO(dto, getEntityClass()));
	}

	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, D dto) {
		E entity = getRepository().getOneByPublicId(id);

		getRepository().save(getMapper().updateFromDTO(dto, entity));
	}

	@GET
	@Path("{id}")
	public D get(@PathParam("id") String id) {
		E User = getRepository().getOneByPublicId(id);
		return getMapper().toDTO(User);
	}

	@GET
	public List<D> listAll() {
		return getRepository().findAll().stream().map(r -> getMapper().toDTO(r)).collect(Collectors.toList());
	}

}
