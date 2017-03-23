package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;
import no.rutebanken.nabu.organisation.model.Id;
import no.rutebanken.nabu.organisation.model.VersionedEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import java.util.List;


public class BaseRepositoryImpl<T extends VersionedEntity> extends SimpleJpaRepository<T, Long> implements VersionedEntityRepository<T> {

	private EntityManager entityManager;
	private JpaEntityInformation<T, Long> entityInformation;

	public BaseRepositoryImpl(JpaEntityInformation<T, Long> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
		this.entityInformation = entityInformation;
	}

	@Override
	public T getOneByPublicIdIfExists(String publicId) {
		Id id = Id.fromString(publicId);

		String jpql = "select e from " + entityInformation.getEntityName() + " e  where e.privateCode=:privateCode";
		if (CodeSpaceEntity.class.isAssignableFrom(getDomainClass())) {
			jpql += " and e.codeSpace.xmlns=:codeSpace";
		}

		TypedQuery<T> query = entityManager.createQuery(jpql, getDomainClass());
		query.setParameter("privateCode", id.getPrivateCode());

		if (CodeSpaceEntity.class.isAssignableFrom(getDomainClass())) {
			query.setParameter("codeSpace", id.getCodeSpace());
		}

		List<T> results = query.getResultList();

		if (results.size() == 1) {
			return results.get(0);
		} else if (results.isEmpty()) {
			return null;
		}
		throw new IllegalArgumentException("Query for one entity returned multiple: " + query);
	}

	@Override
	public T getOneByPublicId(String publicId) {
		T entity = getOneByPublicIdIfExists(publicId);
		if (entity == null) {
			throw new EntityNotFoundException(entityInformation.getEntityName() + " with id: [" + publicId + "] not found");
		}
		return entity;
	}


}
