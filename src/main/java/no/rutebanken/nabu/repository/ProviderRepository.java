package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Provider;

import java.util.Collection;

public interface ProviderRepository {

    Collection<Provider> getProviders();

    Provider getProvider(Long id);

}
