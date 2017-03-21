package no.rutebanken.nabu.organization.rest;

import no.rutebanken.nabu.organization.model.responsibility.EntityType;
import no.rutebanken.nabu.organization.repository.EntityTypeRepository;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.TypeDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organization.rest.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/entity_types")
@Produces("application/json")
@Transactional
public class EntityTypeResource extends BaseResource<EntityType, TypeDTO> {

	@Autowired
	private EntityTypeRepository repository;
	@Autowired
	private TypeMapper<EntityType> mapper;

	@Override
	protected Class<EntityType> getEntityClass() {
		return EntityType.class;
	}

	@Override
	protected VersionedEntityRepository<EntityType> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<EntityType, TypeDTO> getMapper() {
		return mapper;
	}
}
