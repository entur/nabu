package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.SystemJobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class JpaSystemJobStatusRepository extends SimpleJpaRepository<SystemJobStatus, Long> implements SystemJobStatusRepository {


    private EntityManager entityManager;

    public JpaSystemJobStatusRepository(@Autowired EntityManager em) {
        super(SystemJobStatus.class, em);
        entityManager = em;
    }

    @Override
    public List<SystemJobStatus> find(List<String> jobDomains, List<String> jobTypes) {

        StringBuilder jpql = new StringBuilder("select s from SystemJobStatus s ");

        Map<String, Object> parameters = new HashMap<>();

        boolean firstCrit = true;

        if (!CollectionUtils.isEmpty(jobDomains)) {
            jpql.append(firstCrit ? " where " : " and ").append(" s.jobDomain in (:jobDomains) ");
            parameters.put("jobDomains", jobDomains);
            firstCrit = false;
        }

        if (!CollectionUtils.isEmpty(jobTypes)) {
            jpql.append(firstCrit ? " where " : " and ").append(" s.jobType in (:jobTypes) ");
            parameters.put("jobTypes", jobTypes);
            firstCrit = false;
        }

        TypedQuery query = entityManager.createQuery(jpql.toString(), SystemJobStatus.class);
        parameters.forEach((key, value) -> query.setParameter(key, value));
        return query.getResultList();
    }
}
