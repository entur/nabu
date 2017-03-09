package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.SystemStatus;
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
public class JpaSystemStatusRepository implements SystemStatusRepository, DbStatus {


	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void add(SystemStatus systemStatus) {
		entityManager.persist(systemStatus);
	}

	@Override
	public List<SystemStatus> getSystemStatus(Date from, Date to, List<SystemStatus.Action> actions,
			                                         List<SystemStatus.State> states, List<String> entities,
			                                         List<String> sources, List<String> targets) {
		return new SystemStatusQueryBuilder(from, to, actions, states, entities, sources, targets).build().getResultList();
	}


	private class SystemStatusQueryBuilder {

		private StringBuilder sb;

		private Date from;
		private Date to;
		private List<SystemStatus.Action> actions;
		private List<SystemStatus.State> states;
		private List<String> entities;
		private List<String> sources;

		private List<String> targets;


		private Map<String, Object> params = new HashMap();


		public SystemStatusQueryBuilder(Date from, Date to, List<SystemStatus.Action> actions, List<SystemStatus.State> states,
				                               List<String> entities, List<String> sources, List<String> targets) {
			this.from = from;
			this.to = to;
			this.actions = actions;
			this.states = states;
			this.entities = entities;
			this.sources = sources;
			this.targets = targets;
		}

		private TypedQuery<SystemStatus> build() {
			sb = new StringBuilder("SELECT sf FROM SystemStatus sf WHERE sf.correlationId in (select s.correlationId from SystemStatus s ");
			if (from != null) {
				addCriteria("date", ">=", "from", from);
			}
			if (to != null) {
				addCriteria("date", "<=", "to", to);
			}
			if (!CollectionUtils.isEmpty(actions)) {
				addCriteria("action", "in", "actions", actions);
			}
			if (!CollectionUtils.isEmpty(states)) {
				addCriteria("state", "in", "states", states);
			}
			if (!CollectionUtils.isEmpty(entities)) {
				addCriteria("entity", "in", "entities", entities);
			}
			if (!CollectionUtils.isEmpty(sources)) {
				addCriteria("source", "in", "sources", sources);
			}
			if (!CollectionUtils.isEmpty(targets)) {
				addCriteria("target", "in", "targets", targets);
			}

			if (params.isEmpty()) {
				throw new RuntimeException("At least one query param must be set");
			}

			sb.append(") ORDER by sf.correlationId, sf.date");
			TypedQuery<SystemStatus> query = entityManager.createQuery(sb.toString(), SystemStatus.class);
			params.forEach((param, value) -> query.setParameter(param, value));
			return query;
		}

		private void addCriteria(String attribute, String operator, String paramName, Object paramValue) {
			sb.append(params.size() > 0 ? " and " : " where ");
			sb.append(" s." + attribute + " " + operator + " :" + paramName);
			params.put(paramName, paramValue);
		}
	}


	@Override
	public boolean isDbUp() {
		return isPostgresUp(entityManager, logger);
	}
}
