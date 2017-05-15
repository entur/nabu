package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.rutebanken.nabu.repository.DbStatusChecker.isPostgresUp;

@Repository
@Transactional
public class JpaEventRepository extends SimpleJpaRepository<Event, Long> implements EventRepository, DbStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EntityManager entityManager;

    public JpaEventRepository(@Autowired EntityManager em) {
        super(Event.class, em);
        entityManager = em;
    }


    @Override
    public List<JobEvent> findTimetableJobEvents(Long providerId, Instant from, Instant to, List<String> actions, List<JobState> states, List<String> externalIds, List<String> fileNames) {
        StringBuilder sb = new StringBuilder("SELECT sf FROM JobEvent sf WHERE sf.correlationId in " +
                                                     "(select s.correlationId from JobEvent s where  s.domain=:domain  " +
                                                     "and s.providerId = :providerId");

        Map<String, Object> params = new HashMap<>();
        if (from != null) {
            params.put("from", from);
            sb.append(" and s.eventTime>=:from");
        }
        if (to != null) {
            params.put("to", to);
            sb.append(" and s.eventTime<=:to");
        }
        if (!CollectionUtils.isEmpty(actions)) {
            params.put("actions", actions);
            sb.append(" and s.action in (:actions)");
        }
        if (!CollectionUtils.isEmpty(states)) {
            params.put("states", states);
            sb.append(" and s.state in (:states)");
        }
        if (!CollectionUtils.isEmpty(externalIds)) {
            params.put("externalId", externalIds);
            sb.append(" and s.externalId in (:externalId)");
        }
        if (!CollectionUtils.isEmpty(fileNames)) {
            params.put("names", fileNames);
            sb.append(" and s.name in (:names)");
        }

        if (params.isEmpty()) {
            // Use simpler and faster query if only providerId is set
            return getAllJobEventsForProvider(JobEvent.JobDomain.TIMETABLE.toString(), providerId);
        }

        sb.append(") ORDER by sf.correlationId, sf.eventTime");
        TypedQuery<JobEvent> query = entityManager.createQuery(sb.toString(), JobEvent.class);
        params.put("providerId", providerId);
        params.put("domain", JobEvent.JobDomain.TIMETABLE.toString());
        params.forEach((param, value) -> query.setParameter(param, value));

        return query.getResultList();
    }

    private List<JobEvent> getAllJobEventsForProvider(String domain, Long providerId) {
        return this.entityManager.createQuery("SELECT e FROM JobEvent e WHERE e.domain=:domain and" +
                                                      " e.providerId = :providerId ORDER by e.correlationId, e.eventTime", JobEvent.class)
                       .setParameter("providerId", providerId)
                       .setParameter("domain", domain)
                       .getResultList();
    }


    @Override
    public List<JobEvent> getLatestTimetableFileTransfer(Long providerId) {
        return this.entityManager.createQuery("select s1 from JobEvent s1 where s1.domain=:domain and s1.correlationId= " +
                                                      "(select s2.correlationId from JobEvent s2 where  s2.domain=:domain and s2.action=:action and s2.eventTime=" +
                                                      "(select max(s3.eventTime) from JobEvent s3 where  s3.domain=:domain and s3.providerId=:providerId and s3.action=:action and s3.name not like :fileNameExcludePattern))")
                       .setParameter("action", TimeTableAction.FILE_TRANSFER.toString())
                       .setParameter("providerId", providerId)
                       .setParameter("fileNameExcludePattern", "reimport-%")
                       .setParameter("domain", JobEvent.JobDomain.TIMETABLE.toString())
                       .getResultList();
    }

    @Override
    public void clearAll(String domain) {
        this.entityManager.createQuery("delete from JobEvent je where je.domain=:domain").setParameter("domain", domain).executeUpdate();
    }


    @Override
    public void clear(String domain, Long providerId) {
        this.entityManager.createQuery("delete from JobEvent je where je.domain=:domain and je.providerId=:providerId").setParameter("domain", domain).setParameter("providerId", providerId).executeUpdate();
    }


    @Override
    public boolean isDbUp() {
        return isPostgresUp(entityManager, logger);
    }

}
