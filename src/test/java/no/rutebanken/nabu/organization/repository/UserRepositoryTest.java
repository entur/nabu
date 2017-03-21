package no.rutebanken.nabu.organization.repository;

import no.rutebanken.nabu.organization.model.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class UserRepositoryTest extends CommonRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testInsertUser() {
		User user = new User();
		user.setUsername("raffen");
		user.setPrivateCode("2");
		user.setOrganisation(defaultOrganisation);
		User createdUser = userRepository.saveAndFlush(user);

		User fetchedUser = userRepository.getOne(createdUser.getPk());
		Assert.assertTrue(fetchedUser.getId().equals("User:2"));
	}

}

