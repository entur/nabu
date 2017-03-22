package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.model.user.User;

import java.util.List;

public interface UserRepositoryCustom {

	List<User> findUsersWithResponsibilitySet(ResponsibilitySet responsibilitySet);
}
