package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.SystemJobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@Transactional
public class JpaSystemJobStatusRepository extends SimpleJpaRepository<SystemJobStatus, Long> implements SystemJobStatusRepository {


    private EntityManager entityManager;

    public JpaSystemJobStatusRepository(@Autowired EntityManager em) {
        super(SystemJobStatus.class, em);
        entityManager = em;
    }

}
