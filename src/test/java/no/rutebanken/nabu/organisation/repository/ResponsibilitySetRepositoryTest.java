package no.rutebanken.nabu.organisation.repository;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.model.responsibility.Role;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class ResponsibilitySetRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ResponsibilitySetRepository responsibilitySetRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Test
    public void findResponsibilitySetsReferringToRole() {
        Role role1 = createRole("r1");
        Role role2 = createRole("r2");

        ResponsibilitySet only1 = createSet("setOnly1", createRoleAssignment(role1));
        ResponsibilitySet only2 = createSet("setOnly2", createRoleAssignment(role2));
        ResponsibilitySet both = createSet("setBoth", createRoleAssignment(role1), createRoleAssignment(role2));

        Assert.assertEquals(Sets.newHashSet(only1, both), new HashSet<>(responsibilitySetRepository.getResponsibilitySetsReferringTo(role1)));
        Assert.assertEquals(Sets.newHashSet(only2, both), new HashSet<>(responsibilitySetRepository.getResponsibilitySetsReferringTo(role2)));
    }

    private ResponsibilitySet createSet(String name, ResponsibilityRoleAssignment... roles) {
        ResponsibilitySet responsibilitySet = new ResponsibilitySet();

        responsibilitySet.setName(name);
        responsibilitySet.setPrivateCode(name);
        responsibilitySet.setCodeSpace(defaultCodeSpace);

        if (roles != null) {
            responsibilitySet.getRoles().addAll(Arrays.asList(roles));
        }

        return responsibilitySetRepository.save(responsibilitySet);
    }

    private ResponsibilityRoleAssignment createRoleAssignment(Role role) {
        return ResponsibilityRoleAssignment.builder().withCodeSpace(defaultCodeSpace).withPrivateCode(UUID.randomUUID().toString())
                       .withResponsibleOrganisation(defaultOrganisation).withTypeOfResponsibilityRole(role).build();
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setPrivateCode(roleName);
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
