package no.rutebanken.nabu.repository;

import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.domain.event.*;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.EventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class JpaEventRepository extends SimpleJpaRepository<Event, Long> implements EventRepository {

    private EntityManager entityManager;

    public JpaEventRepository(@Autowired EntityManager em) {
        super(Event.class, em);
        entityManager = em;
    }


    @Override
    public List<? extends Event> findEventMatchingFilter(EventFilter eventFilter, Instant from, Instant to) {
        if (eventFilter instanceof CrudEventFilter) {
            return findEventMatchingFilter((CrudEventFilter) eventFilter, from, to);
        } else if (eventFilter instanceof JobEventFilter) {
            return findEventMatchingFilter((JobEventFilter) eventFilter, from, to);
        }
        throw new IllegalArgumentException("Unsupported event filter type: " + eventFilter);
    }


    public List<CrudEvent> findEventMatchingFilter(CrudEventFilter eventFilter, Instant from, Instant to) {
        StringBuilder jpql = new StringBuilder("select e from CrudEvent e where e.registeredTime > :from and e.registeredTime <= :to " +
                                                       "and e.entityType=:entityType");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("to", to);
        parameters.put("entityType", eventFilter.getEntityClassification().getEntityType().getPrivateCode());

        if (!"*".equals(eventFilter.getEntityClassification().getPrivateCode())) {
            jpql.append(" and e.entityClassification=:entityClassification");
            parameters.put("entityClassification", eventFilter.getEntityClassification().getPrivateCode());
        }

        if (CollectionUtils.isEmpty(eventFilter.getAdministrativeZones())) {
            jpql.append(" and e.point in (:polygons)");
            Set<Polygon> polygons = eventFilter.getAdministrativeZones().stream().map(az -> az.getPolygon()).collect(Collectors.toSet());
            parameters.put("polygons", polygons);
        }

        TypedQuery<CrudEvent> query = entityManager.createQuery(jpql.toString(), CrudEvent.class);

        parameters.forEach((name, value) -> query.setParameter(name, value));

        return query.getResultList();
    }

    public List<JobEvent> findEventMatchingFilter(JobEventFilter eventFilter, Instant from, Instant to) {
        StringBuilder jpql = new StringBuilder("select e from JobEvent e where e.registeredTime > :from and e.registeredTime <= :to");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("to", to);


        if (eventFilter.getAction() != null) {
            jpql.append(" and e.action=:action");
            parameters.put("action", eventFilter.getAction());
        }
        if (eventFilter.getState() != null) {
            jpql.append(" and e.state=:state");
            parameters.put("state", eventFilter.getState());
        }

        TypedQuery<JobEvent> query = entityManager.createQuery(jpql.toString(), JobEvent.class);

        parameters.forEach((name, value) -> query.setParameter(name, value));

        return query.getResultList();
    }

    @Override
    public List<JobEvent> findJobEvents(String domain, Long providerId, Instant from, Instant to, List<EventAction> actions, List<String> actionSubTypes, List<JobState> states, List<String> externalIds, List<String> fileNames) {
        StringBuilder sb = new StringBuilder("SELECT sf FROM JobEvent sf WHERE sf.correlationId in (select s.correlationId from JobEvent s where s.providerId = :providerId");

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
        if (!CollectionUtils.isEmpty(actionSubTypes)) {
            params.put("actionSubTypes", actionSubTypes);
            sb.append(" and s.actionSubType in (:actionSubTypes)");
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
            return getAllJobEventsForProvider(domain, providerId);
        }

        sb.append(") ORDER by sf.correlationId, sf.eventTime");
        TypedQuery<JobEvent> query = entityManager.createQuery(sb.toString(), JobEvent.class);
        params.put("providerId", providerId);
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
    public List<JobEvent> getLatestDeliveryStatusForProvider(String domain, Long providerId) {
        return this.entityManager.createQuery("select s1 from JobEvent s1 where s1.domain=:domain and s1.correlationId= " +
                                                      "(select s2.correlationId from JobEvent s2 where  s2.domain=:domain and s2.actionSubType=:actionSubType and s2.eventTime=" +
                                                      "(select max(s3.eventTime) from JobEvent s3 where  s3.domain=:domain and s3.providerId=:providerId and s3.actionSubType=:actionSubType and s3.name not like :fileNameExcludePattern))")
                       .setParameter("actionSubType", TimeTableActionSubType.FILE_TRANSFER.toString())
                       .setParameter("providerId", providerId)
                       .setParameter("fileNameExcludePattern", "reimport-%")
                       .setParameter("domain",domain)
                       .getResultList();
    }

    // TODO notifications

    @Override
    public void clearAll(String domain) {
        this.entityManager.createQuery("delete from JobEvent je where je.domain=:domain").setParameter("domain", domain).executeUpdate();
    }


    @Override
    public void clear(String domain, Long providerId) {
        this.entityManager.createQuery("delete from JobEvent je where je.domain=:domain and je.providerId=:providerId").setParameter("domain", domain).setParameter("providerId", providerId).executeUpdate();
    }

}
