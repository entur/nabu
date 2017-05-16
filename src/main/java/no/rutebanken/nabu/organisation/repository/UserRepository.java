package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.user.User;


public interface UserRepository extends VersionedEntityRepository<User>, UserRepositoryCustom {

    User getUserByUsername(String username);
}
