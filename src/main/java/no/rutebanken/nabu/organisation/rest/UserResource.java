package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.repository.UserRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.mapper.UserMapper;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import no.rutebanken.nabu.organisation.rest.validation.UserValidator;
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
@Path("/users")
@Produces("application/json")
@Transactional
public class UserResource extends BaseResource<User, UserDTO> {

	@Autowired
	private UserRepository repository;
	@Autowired
	private UserMapper mapper;
	@Autowired
	private UserValidator validator;
	@Autowired
	private IamService iamService;


	@POST
	public Response create(UserDTO dto, @Context UriInfo uriInfo) {
		User user = createEntity(dto);
		iamService.createUser(user);
		return buildCreatedResponse(uriInfo, user);
	}

	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, UserDTO dto) {
		User user = updateEntity(id, dto);
		iamService.updateUser(user);
	}


	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) {
		User user = deleteEntity(id);
		iamService.removeUser(user);
	}

	@GET
	public List<UserDTO> listAll() {
		return super.listAllEntities();
	}

	@Override
	protected VersionedEntityRepository<User> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<User, UserDTO> getMapper() {
		return mapper;
	}

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	@Override
	protected DTOValidator<User, UserDTO> getValidator() {
		return validator;
	}

}
