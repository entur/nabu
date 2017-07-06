package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.CrudEventSearch;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
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
                                                     "(select s.correlationId from JobEvent s where  s.domain=:domain  ");

        Map<String, Object> params = new HashMap<>();
        if (providerId != null) {
            params.put("providerId", providerId);
            sb.append("and s.providerId = :providerId");
        }
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

        if (params.isEmpty() || params.size() == 1 && providerId != null) {
            // Use simpler and faster query if no criteria or only providerId is set
            return getJobEventsForDomainAndProvider(JobEvent.JobDomain.TIMETABLE.toString(), providerId);
        }

        sb.append(") ORDER by sf.correlationId, sf.eventTime");
        TypedQuery<JobEvent> query = entityManager.createQuery(sb.toString(), JobEvent.class);
        params.put("domain", JobEvent.JobDomain.TIMETABLE.toString());
        params.forEach((param, value) -> query.setParameter(param, value));

        return query.getResultList();
    }


    @Override
    public List<CrudEvent> findCrudEvents(CrudEventSearch search) {
        StringBuilder sb = new StringBuilder("SELECT e FROM CrudEvent e WHERE type(e)=CrudEvent ");

        Map<String, Object> params = new HashMap<>();
        if (search.getAction() != null) {
            params.put("action", search.getAction());
            sb.append(" and e.action = :action");
        }
        if (search.getFrom() != null) {
            params.put("from", search.getFrom());
            sb.append(" and e.eventTime>=:from");
        }
        if (search.getTo() != null) {
            params.put("to", search.getTo());
            sb.append(" and e.eventTime<=:to");
        }
        if (search.getUsername() != null) {
            params.put("username", search.getUsername());
            sb.append("and e.username = :username");
        }
        if (search.getEntityClassifier() != null) {
            params.put("entityClassifier", search.getEntityClassifier());
            sb.append(" and e.entityClassifier = :entityClassifier");
        }
        if (search.getEntityType() != null) {
            params.put("entityType", search.getEntityType());
            sb.append(" and e.entityType = :entityType");
        }
        if (search.getExternalId() != null) {
            params.put("externalId", search.getExternalId());
            sb.append(" and e.externalId = :externalId");
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("At least one search argument must be specified");
        }

        sb.append(" ORDER by e.eventTime");
        TypedQuery<CrudEvent> query = entityManager.createQuery(sb.toString(), CrudEvent.class);
        params.forEach((param, value) -> query.setParameter(param, value));

        return query.getResultList();
    }

    private List<JobEvent> getJobEventsForDomainAndProvider(String domain, Long providerId) {
        StringBuilder sb = new StringBuilder("SELECT e FROM JobEvent e WHERE e.domain=:domain ");
        Map<String, Object> params = new HashMap<>();
        if (providerId != null) {
            params.put("providerId", providerId);
            sb.append("and e.providerId = :providerId");
        }

        sb.append(" ORDER by e.correlationId, e.eventTime");
        TypedQuery<JobEvent> query = entityManager.createQuery(sb.toString(), JobEvent.class);
        params.put("domain", domain);
        params.forEach((param, value) -> query.setParameter(param, value));

        return query.getResultList();
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
