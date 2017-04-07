package no.rutebanken.nabu.security;


import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.repository.ProviderRepository;
import org.rutebanken.helper.organisation.KeycloakRoleAssignmentExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ProviderAuthenticationService {

    @Autowired
    private ProviderRepository providerRepository;

    public boolean hasRoleForProvider(Authentication authentication, String role, Long providerId) {
        if (providerId == null) {
            return false;
        }
        Provider provider = providerRepository.getProvider(providerId);
        if (provider == null) {
            return false;
        }

        return new KeycloakRoleAssignmentExtractor().getRoleAssignmentsForUser(authentication).stream().filter(ra -> role.equals(ra.r)).anyMatch(ra -> provider.chouetteInfo.xmlns.equals(ra.o));
    }




}
