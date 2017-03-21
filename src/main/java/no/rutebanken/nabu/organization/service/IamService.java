package no.rutebanken.nabu.organization.service;

import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.model.user.User;

public interface IamService {

	void createUser(User user);

	void updateUser(User user);

	void updateResponsibilitySet(ResponsibilitySet responsibilitySet);
}
