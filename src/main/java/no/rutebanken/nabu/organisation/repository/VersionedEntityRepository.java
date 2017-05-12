package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

public interface VersionedEntityRepository<T extends VersionedEntity> extends JpaRepository<T, Long> {
	/** Get one, or throw exception if no entity with id exists */
	T getOneByPublicId(String id);

	/** Get one, or null if no entity with id exists */
	T getOneByPublicIdIfExists(String id);

	@Override
	@QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
	List<T> findAll();
}
