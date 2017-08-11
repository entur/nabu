package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Service
public class UserValidator implements DTOValidator<User, UserDTO> {


    private static final String DEFAULT_PATTERN = "^[a-zA-Z0-9_-[.]]{3,30}$";

    @Value("${username.pattern:" + DEFAULT_PATTERN + "}")
    private String usernamePattern = DEFAULT_PATTERN;

    @Override
    public void validateCreate(UserDTO dto) {
        Assert.hasLength(dto.username, "username required");
        Assert.isTrue(dto.username.matches(usernamePattern),"username must be alphanumeric");
        Assert.hasLength(dto.organisationRef, "organisationRef required");
        assertCommon(dto);
    }

    @Override
    public void validateUpdate(UserDTO dto, User entity) {
        assertCommon(dto);
    }

    private void assertCommon(UserDTO dto) {
        Assert.notNull(dto.contactDetails, "contactDetails required");
        Assert.notNull(dto.contactDetails.email, "contactDetails.email required");
        Assert.isTrue(isValidEmailAddress(dto.contactDetails.email), "contactDetails.email must be a valid email address");

    }

    @Override
    public void validateDelete(User entity) {
        // TODO check whether in user
    }


    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
