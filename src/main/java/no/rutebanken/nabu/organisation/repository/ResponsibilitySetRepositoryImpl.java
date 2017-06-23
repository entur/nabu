package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.model.responsibility.Role;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ResponsibilitySetRepositoryImpl implements ResponsibilitySetRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<ResponsibilitySet> getResponsibilitySetsReferringTo(VersionedEntity entity) {

        if (entity instanceof Role) {
            return entityManager.createQuery("select rs from ResponsibilitySet rs inner join rs.roles r" +
                                                     " where r.typeOfResponsibilityRole=:role", ResponsibilitySet.class)
                           .setParameter("role", entity).getResultList();
        }

        // TODO
        return new ArrayList<>();
    }
}
