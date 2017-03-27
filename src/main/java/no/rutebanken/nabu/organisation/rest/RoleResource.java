package no.rutebanken.nabu.organisation.rest;


import no.rutebanken.nabu.organisation.model.responsibility.Role;
import no.rutebanken.nabu.organisation.repository.RoleRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.mapper.TypeMapper;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import no.rutebanken.nabu.organisation.rest.validation.TypeValidator;
import no.rutebanken.nabu.organisation.service.IamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@Component
@Path("/roles")
@Produces("application/json")
@Transactional
public class RoleResource extends BaseResource<Role, TypeDTO> {

	@Autowired
	private TypeMapper<Role> mapper;

	@Autowired
	private RoleRepository repository;

	@Autowired
	private TypeValidator<Role> validator;


	@Autowired
	private IamService iamService;

	@GET
	@Path("{id}")
	public TypeDTO get(@PathParam("id") String id) {
		return super.getEntity(id);
	}

	@POST
	public Response create(TypeDTO dto, @Context UriInfo uriInfo) {
		Role Role = createEntity(dto);
		iamService.createRole(Role);
		return buildCreatedResponse(uriInfo, Role);
	}

	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, TypeDTO dto) {
		updateEntity(id, dto);
	}


	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) {
		Role Role = deleteEntity(id);
		iamService.removeRole(Role);
	}

	@GET
	public List<TypeDTO> listAll() {
		return super.listAllEntities();
	}


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
