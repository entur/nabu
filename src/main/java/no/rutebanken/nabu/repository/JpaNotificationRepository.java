package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
@Repository
@Transactional
public class JpaNotificationRepository extends SimpleJpaRepository<Notification, Long>  implements NotificationRepository  {

    private EntityManager entityManager;

    public JpaNotificationRepository(@Autowired EntityManager em) {
        super(Notification.class, em);
        entityManager = em;
    }
}
