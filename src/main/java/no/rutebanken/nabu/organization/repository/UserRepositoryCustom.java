package no.rutebanken.nabu.organization.repository;

import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.model.user.User;

import java.util.List;

public interface UserRepositoryCustom {

	List<User> findUsersWithResponsibilitySet(ResponsibilitySet responsibilitySet);
}
