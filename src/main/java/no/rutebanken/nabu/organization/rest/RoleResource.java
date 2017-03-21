package no.rutebanken.nabu.organization.rest;


import no.rutebanken.nabu.organization.model.responsibility.Role;
import no.rutebanken.nabu.organization.repository.RoleRepository;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.TypeDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organization.rest.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/roles")
@Produces("application/json")
@Transactional
public class RoleResource extends BaseResource<Role, TypeDTO> {

	@Autowired
	private TypeMapper<Role> mapper;

	@Autowired
	private RoleRepository repository;

	@Override
	protected Class<Role> getEntityClass() {
		return Role.class;
	}

	@Override
	protected VersionedEntityRepository<Role> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<Role, TypeDTO> getMapper() {
		return mapper;
	}

}
