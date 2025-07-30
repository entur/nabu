package no.rutebanken.nabu.security.oauth2;

import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.authorization.DefaultAuthorizationService;

public class DefaultNabuAuthorizationService extends DefaultAuthorizationService<Long> implements NabuAuthorizationService {

    private final ProviderRepository providerRepository;

    public DefaultNabuAuthorizationService(ProviderRepository providerRepository, RoleAssignmentExtractor roleAssignmentExtractor) {
        super(providerId -> providerRepository.getProvider(providerId) == null ? null : providerRepository.getProvider(providerId).getChouetteInfo().xmlns, roleAssignmentExtractor);
        this.providerRepository = providerRepository;
    }

    @Override
    public boolean canViewTimetableDataEvent(String codespace) {
        Provider provider = providerRepository.getProvider(codespace);
        if (provider == null) {
            return false;
        }
        return canEditRouteData(provider.getId());
    }

}
