package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
        if (dto.notifications != null) {
            for (NotificationConfigDTO notificationConfigDTO : dto.notifications) {

                Assert.notNull(notificationConfigDTO.notificationType, "notifications.notificationType required");
                Assert.notNull(notificationConfigDTO.eventFilter, "notifications.eventFilter required");
                Assert.notNull(notificationConfigDTO.eventFilter.type, "notifications.eventFilter.type required");
                // TODO verify filter in full +  test
            }
        }
    }
}
