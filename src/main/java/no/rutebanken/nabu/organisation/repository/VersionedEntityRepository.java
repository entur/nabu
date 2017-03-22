package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionedEntityRepository<T extends VersionedEntity> extends JpaRepository<T, Long> {
	T getOneByPublicId(String id);
}
