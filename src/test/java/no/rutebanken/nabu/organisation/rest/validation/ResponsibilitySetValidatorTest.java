package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.rest.dto.responsibility.EntityClassificationAssignmentDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilitySetDTO;
import org.junit.Test;

public class ResponsibilitySetValidatorTest {


    private ResponsibilitySetValidator responsibilitySetValidator = new ResponsibilitySetValidator();


    @Test
    public void validateCreateMinimalOk() {
        responsibilitySetValidator.validateCreate(minimalRespSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCreateWithDuplicateEventClassificationRefFails() {
        ResponsibilitySetDTO respSet = minimalRespSet();

        ResponsibilityRoleAssignmentDTO roleAssignment = respSet.roles.get(0);
        String ref = "commonRef";
        roleAssignment.entityClassificationAssignments.add(new EntityClassificationAssignmentDTO(ref, true));
        roleAssignment.entityClassificationAssignments.add(new EntityClassificationAssignmentDTO(ref, false));
        responsibilitySetValidator.validateCreate(respSet);
    }


    private ResponsibilitySetDTO minimalRespSet() {
        ResponsibilitySetDTO responsibilitySet = new ResponsibilitySetDTO();
        responsibilitySet.codeSpace = "testCodeSpace";
        responsibilitySet.privateCode = "testPrivateCode";
        responsibilitySet.name = "testSet";

        ResponsibilityRoleAssignmentDTO roleAssignment = new ResponsibilityRoleAssignmentDTO();

        roleAssignment.typeOfResponsibilityRoleRef = "testRole";
        roleAssignment.responsibleOrganisationRef = "testOrg";

        responsibilitySet.roles.add(roleAssignment);

        return responsibilitySet;
    }
}
