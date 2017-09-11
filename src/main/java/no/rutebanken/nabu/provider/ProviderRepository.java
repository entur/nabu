package no.rutebanken.nabu.provider;

import no.rutebanken.nabu.provider.model.Provider;

import java.util.Collection;

public interface ProviderRepository {

    Collection<Provider> getProviders();

    Provider getProvider(Long id);

}
