package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Provider;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Repository
@Transactional
public class JpaProviderRepository implements ProviderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Collection<Provider> getProviders() {
        return this.entityManager.createQuery("SELECT p FROM Provider p", Provider.class).getResultList();
    }

    @Override
    public Provider getProvider(Long id) {
        return entityManager.find(Provider.class, id);
    }

}
