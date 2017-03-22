package no.rutebanken.nabu.organisation.rest;


import no.rutebanken.nabu.organisation.model.responsibility.Role;
import no.rutebanken.nabu.organisation.repository.RoleRepository;
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
@Path("/roles")
@Produces("application/json")
@Transactional
public class RoleResource extends AnnotatedBaseResource<Role, TypeDTO> {

	@Autowired
	private TypeMapper<Role> mapper;

	@Autowired
	private RoleRepository repository;

	@Autowired
	private TypeValidator<Role> validator;

	@Override
	protected VersionedEntityRepository<Role> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<Role, TypeDTO> getMapper() {
		return mapper;
	}

	@Override
	protected Class<Role> getEntityClass() {
		return Role.class;
	}

	@Override
	protected DTOValidator<Role, TypeDTO> getValidator() {
		return validator;
	}
}
