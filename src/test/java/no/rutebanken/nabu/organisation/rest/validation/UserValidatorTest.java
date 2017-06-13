package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import org.junit.Test;

public class UserValidatorTest {

    private UserValidator userValidator = new UserValidator();


    @Test
    public void validateCreateMinimalOk() {
        userValidator.validateCreate(minimalUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithoutUsernameFails() {
        UserDTO user = minimalUser();
        user.username = null;
        userValidator.validateCreate(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithoutOrganisationFails() {
        UserDTO user = minimalUser();
        user.organisationRef = null;
        userValidator.validateCreate(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithoutContactDetailsFails() {
        UserDTO user = minimalUser();
        user.contactDetails = null;
        userValidator.validateCreate(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithoutEmailFails() {
        UserDTO user = minimalUser();
        user.contactDetails.email = null;
        userValidator.validateCreate(user);
    }

    @Test
    public void validateUpdateMinimalUserOK() {
        userValidator.validateUpdate(minimalUser(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateInvalidEmailFails() {
        UserDTO user = minimalUser();
        user.contactDetails=new ContactDetailsDTO("first","last","34234","illegalEmail");
        userValidator.validateCreate(user);
    }

    @Test
    public void validateValidEmailOK() {
        UserDTO user = minimalUser();
        user.contactDetails=new ContactDetailsDTO("first","last","34234","legal@email.com");
        userValidator.validateCreate(user);
    }

    protected UserDTO minimalUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.username = "username";
        userDTO.organisationRef = "organisation";
        userDTO.contactDetails=new ContactDetailsDTO(null,null,null, "valid@email.org");

        return userDTO;
    }

}
