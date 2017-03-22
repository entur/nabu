package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class UserRepositoryImpl implements UserRepositoryCustom {

	@Autowired
	private EntityManager entityManager;


	@Override
	public List<User> findUsersWithResponsibilitySet(ResponsibilitySet responsibilitySet) {

		TypedQuery<User> query = entityManager.createQuery("select u from User u where :respSet member of u.responsibilitySets", User.class);

		query.setParameter("respSet", responsibilitySet);

		return query.getResultList();
	}
}
