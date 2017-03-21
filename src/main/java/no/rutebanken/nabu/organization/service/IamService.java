package no.rutebanken.nabu.organization.service;

import no.rutebanken.nabu.organization.model.user.User;

public interface IamService {

	void createUser(User user);

	void updateUser(User user);
}
