package no.rutebanken.nabu.organization.repository;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.model.responsibility.Role;
import no.rutebanken.nabu.organization.model.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class UserRepositoryTest extends CommonRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ResponsibilitySetRepository responsibilitySetRepository;


	@Test
	public void testInsertUser() {
		User user = User.builder().withUsername("raffen").withPrivateCode("2").withOrganisation(defaultOrganisation).build();
		User createdUser = userRepository.saveAndFlush(user);

		User fetchedUser = userRepository.getOne(createdUser.getPk());
		Assert.assertTrue(fetchedUser.getId().equals("User:2"));
	}


	@Test
	public void testFindByResponsibilitySet() {
		Role role = roleRepository.save(new Role("testCode", "testRole"));
		ResponsibilityRoleAssignment responsibilityRoleAssignment =
				ResponsibilityRoleAssignment.builder().withPrivateCode("pCode").withResponsibleOrganisation(defaultOrganisation)
						.withTypeOfResponsibilityRole(role).withCodeSpace(defaultCodeSpace).build();
		ResponsibilitySet responsibilitySet = new ResponsibilitySet(defaultCodeSpace, "pCode", "name", Sets.newHashSet(responsibilityRoleAssignment));

		responsibilitySet = responsibilitySetRepository.save(responsibilitySet);

		User userWithRespSet =
				userRepository.saveAndFlush(User.builder()
						                            .withUsername("userWithRespSet").withPrivateCode("userWithRespSet")
						                            .withOrganisation(defaultOrganisation)
						                            .withResponsibilitySets(Sets.newHashSet(responsibilitySet))
						                            .build());
		User userWithoutRespSet = userRepository.saveAndFlush(User.builder().withUsername("userWithoutRespSet").withPrivateCode("userWithoutRespSet").withOrganisation(defaultOrganisation).build());

		List<User> usersWithRespSet = userRepository.findUsersWithResponsibilitySet(responsibilitySet);

		Assert.assertEquals(1, usersWithRespSet.size());
		Assert.assertTrue(usersWithRespSet.contains(userWithRespSet));
		Assert.assertFalse(usersWithRespSet.contains(userWithoutRespSet));
	}

}

