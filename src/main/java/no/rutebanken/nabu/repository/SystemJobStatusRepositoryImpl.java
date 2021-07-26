/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobState;
import org.hibernate.annotations.QueryHints;
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
public class SystemJobStatusRepositoryImpl extends SimpleJpaRepository<SystemJobStatus, Long> implements SystemJobStatusRepository {


    private EntityManager entityManager;

    public SystemJobStatusRepositoryImpl(@Autowired EntityManager em) {
        super(SystemJobStatus.class, em);
        entityManager = em;
    }

    @Override
    public List<SystemJobStatus> find(List<String> jobDomains, List<String> actions) {

        StringBuilder jpql = new StringBuilder("select s from SystemJobStatus s ");

        Map<String, Object> parameters = new HashMap<>();

        boolean firstCrit = true;

        if (!CollectionUtils.isEmpty(jobDomains)) {
            jpql.append(" where ").append(" s.jobDomain in (:jobDomains) ");
            parameters.put("jobDomains", jobDomains);
            firstCrit = false;
        }

        if (!CollectionUtils.isEmpty(actions)) {
            jpql.append(firstCrit ? " where " : " and ").append(" s.action in (:actions) ");
            parameters.put("actions", actions);
        }

        TypedQuery<SystemJobStatus> query = entityManager.createQuery(jpql.toString(), SystemJobStatus.class);
        query.setHint(QueryHints.CACHEABLE, Boolean.TRUE);
        parameters.forEach(query::setParameter);
        return query.getResultList();
    }

    @Override
    public SystemJobStatus findByJobDomainAndActionAndState(String jobDomain, String action, JobState state) {
        return entityManager.createQuery("select s from SystemJobStatus s where s.jobDomain=:jobDomain " +
                                                 "and s.action=:action and s.state=:state", SystemJobStatus.class)
                       .setParameter("jobDomain", jobDomain).setParameter("action", action)
                       .setParameter("state", state).setHint(QueryHints.CACHEABLE, Boolean.TRUE).getSingleResult();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
