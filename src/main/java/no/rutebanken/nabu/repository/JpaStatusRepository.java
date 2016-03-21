package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

import static no.rutebanken.nabu.repository.DbStatusChecker.isPostgresUp;

@Repository
@Transactional
public class JpaStatusRepository implements StatusRepository, DbStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Status update(Status status) {
        return entityManager.merge(status);
    }

    @Override
    public Collection<Status> getStatusForProvider(Long providerId) {
        return this.entityManager.createQuery("SELECT s FROM Status s WHERE s.providerId = :providerId", Status.class)
                .setParameter("providerId", providerId)
                .getResultList();
    }

    @Override
    public boolean isDbUp() {
        return isPostgresUp(entityManager, logger);
    }

}
