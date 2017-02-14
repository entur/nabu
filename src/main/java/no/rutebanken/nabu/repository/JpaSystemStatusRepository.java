package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
	public boolean isDbUp() {
		return isPostgresUp(entityManager, logger);
	}
}
