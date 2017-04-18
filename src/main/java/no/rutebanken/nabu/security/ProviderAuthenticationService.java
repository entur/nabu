package no.rutebanken.nabu.security;


import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.repository.ProviderRepository;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ProviderAuthenticationService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private RoleAssignmentExtractor roleAssignmentExtractor;


    @Value("${authorization.enabled:true}")
    protected boolean authorizationEnabled;


    public boolean hasRoleForProvider(Authentication authentication, String role, Long providerId) {
        if (!authorizationEnabled) {
            return true;
        }
        if (providerId == null) {
            return false;
        }
        Provider provider = providerRepository.getProvider(providerId);
        if (provider == null) {
            return false;
        }

        return roleAssignmentExtractor.getRoleAssignmentsForUser(authentication).stream()
                       .filter(ra -> role.equals(ra.r)).anyMatch(ra -> provider.chouetteInfo.xmlns.equals(ra.o));
    }


}
