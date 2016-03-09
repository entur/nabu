package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Status;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Repository
@Transactional
public class JpaStatusRepository implements StatusRepository {

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

}
