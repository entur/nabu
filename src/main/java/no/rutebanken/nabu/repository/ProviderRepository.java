package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Provider;

import java.util.Collection;

public interface ProviderRepository {

    Collection<Provider> getProviders();

    Provider getProvider(Long id);
    
    void updateProvider(Provider provider);

	Provider createProvider(Provider provider);

	void deleteProvider(Long providerId);

}
