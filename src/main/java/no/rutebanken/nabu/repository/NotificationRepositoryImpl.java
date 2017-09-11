package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Transactional
public class NotificationRepositoryImpl extends SimpleJpaRepository<Notification, Long> implements NotificationRepository {

    private EntityManager entityManager;

    public NotificationRepositoryImpl(@Autowired EntityManager em) {
        super(Notification.class, em);
        entityManager = em;
    }

    @Override
    public List<Notification> findByTypeAndStatus(NotificationType notificationType, Notification.NotificationStatus status) {
        return entityManager.createQuery("select n from Notification n where n.type=:type and n.status=:status", Notification.class)
                       .setParameter("type", notificationType).setParameter("status", status).getResultList();
    }

    @Override
    public List<Notification> findByUserNameAndTypeAndStatus(String userName, NotificationType notificationType, Notification.NotificationStatus status) {
        return entityManager.createQuery("select n from Notification n where n.type=:type and n.userName=:userName and n.status=:status", Notification.class)
                       .setParameter("type", notificationType).setParameter("userName", userName).setParameter("status", status).getResultList();

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
