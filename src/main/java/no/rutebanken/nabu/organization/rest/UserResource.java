package no.rutebanken.nabu.organization.rest;

import no.rutebanken.nabu.organization.model.user.User;
import no.rutebanken.nabu.organization.repository.UserRepository;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.user.UserDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organization.rest.mapper.UserMapper;
import no.rutebanken.nabu.organization.service.IamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Component
@Path("/users")
@Produces("application/json")
@Transactional
public class UserResource extends BaseResource<User, UserDTO> {

	@Autowired
	private UserRepository repository;
	@Autowired
	private UserMapper mapper;
	@Autowired
	private IamService iamService;

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	@Override
	protected VersionedEntityRepository<User> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<User, UserDTO> getMapper() {
		return mapper;
	}

	@POST
	public Response create(UserDTO dto, @Context UriInfo uriInfo) {
		User user = getRepository().save(getMapper().createFromDTO(dto, getEntityClass()));
		iamService.createUser(user);
		return buildCreatedResponse(uriInfo, user);
	}

	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, UserDTO dto) {
		User user = getExisting(id);
		repository.save(mapper.updateFromDTO(dto, user));
		iamService.updateUser(user);
	}

}
