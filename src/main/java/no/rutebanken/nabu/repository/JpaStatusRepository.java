package no.rutebanken.nabu.repository;

import static no.rutebanken.nabu.repository.DbStatusChecker.isPostgresUp;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import no.rutebanken.nabu.domain.Status;

@Repository
@Transactional
public class JpaStatusRepository implements StatusRepository, DbStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Status status) {
        entityManager.persist(status);
    }

    @Override
    public List<Status> getStatusForProvider(Long providerId) {
        return this.entityManager.createQuery("SELECT s FROM Status s WHERE s.providerId = :providerId ORDER by s.correlationId, s.date", Status.class)
                .setParameter("providerId", providerId)
                .getResultList();
    }

    @Override
    public boolean isDbUp() {
        return isPostgresUp(entityManager, logger);
    }

}
