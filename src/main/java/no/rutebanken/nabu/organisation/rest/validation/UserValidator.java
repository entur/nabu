package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Service
public class UserValidator implements DTOValidator<User, UserDTO> {

    @Override
    public void validateCreate(UserDTO dto) {
        Assert.hasLength(dto.username, "username required");
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
