package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.repository.UserRepository;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilitySetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ResponsibilitySetValidator implements DTOValidator<ResponsibilitySet, ResponsibilitySetDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void validateCreate(ResponsibilitySetDTO dto) {
        Assert.hasLength(dto.codeSpace, "codeSpace required");
        Assert.hasLength(dto.privateCode, "privateCode required");
        assertCommon(dto);
    }

    @Override
    public void validateUpdate(ResponsibilitySetDTO dto, ResponsibilitySet entity) {
        assertCommon(dto);
    }


    private void assertCommon(ResponsibilitySetDTO dto) {
        Assert.hasLength(dto.name, "name required");
        Assert.notEmpty(dto.roles, "roles required");
        for (ResponsibilityRoleAssignmentDTO roleDto : dto.roles) {
            Assert.notNull(roleDto, "roles cannot be empty");
            Assert.hasLength(roleDto.typeOfResponsibilityRoleRef, "roles.typeOfResponsibilityRoleRef required");
            Assert.hasLength(roleDto.responsibleOrganisationRef, "roles.responsibleOrganisationRef required");
        }
    }

    @Override
    public void validateDelete(ResponsibilitySet entity) {
        Assert.isTrue(userRepository.findUsersWithResponsibilitySet(entity).isEmpty(), "referred to by user");
    }
}
