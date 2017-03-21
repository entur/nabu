package no.rutebanken.nabu.organization.repository;

import no.rutebanken.nabu.organization.model.VersionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionedEntityRepository<T extends VersionedEntity> extends JpaRepository<T, Long> {
	T getOneByPublicId(String id);
}
