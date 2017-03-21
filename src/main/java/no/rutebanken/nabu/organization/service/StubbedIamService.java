package no.rutebanken.nabu.organization.service;

import no.rutebanken.nabu.organization.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StubbedIamService implements IamService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void createUser(User user) {
		logger.info("Received createUser: " + user);
	}

	@Override
	public void updateUser(User user) {
		logger.info("Received updateUser: " + user);
	}
}
