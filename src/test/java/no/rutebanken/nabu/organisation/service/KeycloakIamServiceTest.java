package no.rutebanken.nabu.organisation.service;

import no.rutebanken.nabu.organisation.model.organisation.Authority;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
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

        EntityClassification entityClassification = new EntityClassification();
        entityClassification.setPrivateCode("*");
        EntityType entityType = new EntityType();
        entityType.setPrivateCode("StopPlace");
        entityClassification.setEntityType(entityType);

        orgRegRoleAssignment.getResponsibleEntityClassifications().add(entityClassification);

        orgRegRoleAssignment.setTypeOfResponsibilityRole(role);
        orgRegRoleAssignment.setResponsibleOrganisation(organisation);


        RoleAssignment keycloakRoleAssignment = new KeycloakIamService().toRoleAssignment(orgRegRoleAssignment);

        Assert.assertEquals(role.getPrivateCode(), keycloakRoleAssignment.getRole());
        Assert.assertEquals(organisation.getPrivateCode(), keycloakRoleAssignment.getOrganisation());
        Assert.assertEquals(Arrays.asList(entityClassification.getPrivateCode()), keycloakRoleAssignment.getEntityClassifications().get(entityType.getPrivateCode()));
        Assert.assertNull(keycloakRoleAssignment.getAdministrativeZone());
    }


}
