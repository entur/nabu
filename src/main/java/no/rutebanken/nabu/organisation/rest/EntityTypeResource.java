package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.model.responsibility.EntityType;
import no.rutebanken.nabu.organisation.repository.EntityTypeRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.mapper.TypeMapper;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import no.rutebanken.nabu.organisation.rest.validation.TypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/entity_types")
@Produces("application/json")
@Transactional
public class EntityTypeResource extends AnnotatedBaseResource<EntityType, TypeDTO> {

	@Autowired
	private EntityTypeRepository repository;
	@Autowired
	private TypeMapper<EntityType> mapper;
	@Autowired
	private TypeValidator<EntityType> validator;

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

	@Override
	protected DTOValidator<EntityType, TypeDTO> getValidator() {
		return validator;
	}
}
