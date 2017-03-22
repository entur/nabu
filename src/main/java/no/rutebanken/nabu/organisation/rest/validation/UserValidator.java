package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserValidator implements DTOValidator<User,UserDTO> {

	@Override
	public void validateCreate(UserDTO dto) {
		Assert.hasLength(dto.username, "username required");
		Assert.hasLength(dto.organisationRef, "organisationRef required");
	}

	@Override
	public void validateUpdate(UserDTO dto, User entity) {

	}
}
