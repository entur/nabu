package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@Transactional
public class JpaNotificationRepository extends SimpleJpaRepository<Notification, Long> implements NotificationRepository {

    private EntityManager entityManager;

    public JpaNotificationRepository(@Autowired EntityManager em) {
        super(Notification.class, em);
        entityManager = em;
    }

    @Override
    public void clearAll(String domain) {
        this.entityManager.createQuery("delete from Notification n where n.event in (select je from JobEvent je where je.domain=:domain)").setParameter("domain", domain).executeUpdate();
    }


    @Override
    public void clear(String domain, Long providerId) {
        this.entityManager.createQuery("delete from Notification n where  n.event in (select je from JobEvent je where je.domain=:domain and je.providerId=:providerId)").setParameter("domain", domain).setParameter("providerId", providerId).executeUpdate();
    }
}
