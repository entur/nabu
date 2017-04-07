package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.model.responsibility.EntityType;
import no.rutebanken.nabu.organisation.repository.EntityTypeRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.EntityTypeDTO;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.mapper.EntityTypeMapper;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import no.rutebanken.nabu.organisation.rest.validation.EntityTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("/entity_types")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
public class EntityTypeResource extends AnnotatedBaseResource<EntityType, EntityTypeDTO> {

	@Autowired
	private EntityTypeRepository repository;
	@Autowired
	private EntityTypeMapper mapper;
	@Autowired
	private EntityTypeValidator validator;

	@Override
	protected Class<EntityType> getEntityClass() {
		return EntityType.class;
	}

	@Override
	protected VersionedEntityRepository<EntityType> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<EntityType, EntityTypeDTO> getMapper() {
		return mapper;
	}

	@Override
	protected DTOValidator<EntityType, EntityTypeDTO> getValidator() {
		return validator;
	}
}
