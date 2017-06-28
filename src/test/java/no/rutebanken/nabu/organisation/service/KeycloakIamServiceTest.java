package no.rutebanken.nabu.organisation.service;

import no.rutebanken.nabu.organisation.model.organisation.Authority;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassificationAssignment;
import no.rutebanken.nabu.organisation.model.responsibility.EntityType;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organisation.model.responsibility.Role;
import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.helper.organisation.RoleAssignment;

import java.util.Arrays;

public class KeycloakIamServiceTest {


    @Test
    public void testMapResponsibilityRoleAssignmentToKeycloakRoleAssignment() {
        ResponsibilityRoleAssignment orgRegRoleAssignment = new ResponsibilityRoleAssignment();
        Role role = new Role();
        role.setPrivateCode("testRole");
        Organisation organisation = new Authority();
        organisation.setPrivateCode("testOrg");

        EntityType entityType = new EntityType();
        entityType.setPrivateCode("StopPlace");

        EntityClassification entityClassification = new EntityClassification();
        entityClassification.setPrivateCode("*");
        entityClassification.setEntityType(entityType);
        EntityClassificationAssignment entityClassificationAssignment = new EntityClassificationAssignment(entityClassification, true);
        orgRegRoleAssignment.getResponsibleEntityClassifications().add(entityClassificationAssignment);

        EntityClassification entityClassificationNegated = new EntityClassification();
        entityClassificationNegated.setPrivateCode("buss");
        entityClassificationNegated.setEntityType(entityType);
        EntityClassificationAssignment entityClassificationAssignmentNegated = new EntityClassificationAssignment(entityClassificationNegated, false);
        orgRegRoleAssignment.getResponsibleEntityClassifications().add(entityClassificationAssignmentNegated);


        orgRegRoleAssignment.setTypeOfResponsibilityRole(role);
        orgRegRoleAssignment.setResponsibleOrganisation(organisation);


        RoleAssignment keycloakRoleAssignment = new KeycloakIamService().toRoleAssignment(orgRegRoleAssignment);

        Assert.assertEquals(role.getPrivateCode(), keycloakRoleAssignment.getRole());
        Assert.assertEquals(organisation.getPrivateCode(), keycloakRoleAssignment.getOrganisation());
        Assert.assertEquals(Arrays.asList(entityClassification.getPrivateCode(), "!" + entityClassificationNegated.getPrivateCode()),
                keycloakRoleAssignment.getEntityClassifications().get(entityType.getPrivateCode()));
        Assert.assertNull(keycloakRoleAssignment.getAdministrativeZone());
    }


}
