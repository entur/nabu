package no.rutebanken.nabu.organisation.service;

import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.model.user.User;

public interface IamService {

	void createUser(User user);

	void updateUser(User user);

	void removeUser(User user);

	void updateResponsibilitySet(ResponsibilitySet responsibilitySet);
}
