package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionedEntityRepository<T extends VersionedEntity> extends JpaRepository<T, Long> {
	/** Get one, or throw exception if no entity with id exists */
	T getOneByPublicId(String id);

	/** Get one, or null if no entity with id exists */
	T getOneByPublicIdIfExists(String id);
}
