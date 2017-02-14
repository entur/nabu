package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.rutebanken.nabu.repository.DbStatusChecker.isPostgresUp;

@Repository
@Transactional
public class JpaStatusRepository implements StatusRepository, DbStatus {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void add(Status status) {
		entityManager.persist(status);
	}

	@Override
	public List<Status> getStatusForProvider(Long providerId, Date from, Date to, List<Status.Action> actions, List<Status.State> states, List<Long> jobIds, List<String> fileNames) {
		StringBuilder sb = new StringBuilder("SELECT sf FROM Status sf WHERE sf.correlationId in (select s.correlationId from Status s where s.providerId = :providerId");

		Map<String, Object> params = new HashMap();
		if (from != null) {
			params.put("from", from);
			sb.append(" and s.date>=:from");
		}
		if (to != null) {
			params.put("to", to);
			sb.append(" and s.date<=:to");
		}
		if (!CollectionUtils.isEmpty(actions)) {
			params.put("actions", actions);
			sb.append(" and s.action in (:actions)");
		}
		if (!CollectionUtils.isEmpty(states)) {
			params.put("states", states);
			sb.append(" and s.state in (:states)");
		}
		if (!CollectionUtils.isEmpty(jobIds)) {
			params.put("jobIds", jobIds);
			sb.append(" and s.jobId in (:jobIds)");
		}
		if (!CollectionUtils.isEmpty(fileNames)) {
			params.put("fileNames", fileNames);
			sb.append(" and s.fileName in (:fileNames)");
		}

		if (params.isEmpty()){
			// Use simpler and faster query if only providerId is set
			return getAllStatusForProvider(providerId);
		}

		sb.append(") ORDER by sf.correlationId, sf.date");
		TypedQuery<Status> query = entityManager.createQuery(sb.toString(), Status.class);
		params.put("providerId",providerId);
		params.forEach((param, value) -> query.setParameter(param, value));

		return query.getResultList();


	}


	private List<Status> getAllStatusForProvider(Long providerId) {
		return this.entityManager.createQuery("SELECT s FROM Status s WHERE s.providerId = :providerId ORDER by s.correlationId, s.date", Status.class)
				       .setParameter("providerId", providerId)
				       .getResultList();
	}


	@Override
	public boolean isDbUp() {
		return isPostgresUp(entityManager, logger);
	}

}
