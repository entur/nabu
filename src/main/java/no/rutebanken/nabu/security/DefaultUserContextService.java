package no.rutebanken.nabu.security;

import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.*;

public class DefaultUserContextService implements UserContextService {

    private final ProviderRepository providerRepository;
    private final RoleAssignmentExtractor roleAssignmentExtractor;
    private final boolean authorizationEnabled;

    public DefaultUserContextService(ProviderRepository providerRepository,
                                     RoleAssignmentExtractor roleAssignmentExtractor,
                                     boolean authorizationEnabled) {
        this.providerRepository = providerRepository;
        this.roleAssignmentExtractor = roleAssignmentExtractor;
        this.authorizationEnabled = authorizationEnabled;
    }


    @Override
    public boolean isRouteDataAdmin() {
        return isAdminFor(ROLE_ROUTE_DATA_ADMIN);
    }

    @Override
    public boolean isOrganizationAdmin() {
        // ROLE_ORGANISATION_EDIT provides admin privilege on all organizations
        return isAdminFor(ROLE_ORGANISATION_EDIT);
    }


    private boolean isAdminFor(String role) {

        if (!authorizationEnabled) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<RoleAssignment> roleAssignments =
                roleAssignmentExtractor.getRoleAssignmentsForUser(auth);

        return roleAssignments
                .stream()
                .anyMatch(roleAssignment ->
                        roleAssignment.getRole().equals(role) &&
                                roleAssignment.getOrganisation().equals("RB")
                );
    }

    @Override
    public boolean canEditProvider(Long providerId) {

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<RoleAssignment> roleAssignments =
                roleAssignmentExtractor.getRoleAssignmentsForUser(auth);

        return roleAssignments
                .stream()
                .anyMatch(roleAssignment ->
                        (
                                roleAssignment.getRole().equals(ROLE_ROUTE_DATA_ADMIN) &&
                                        roleAssignment.getOrganisation().equals("RB")
                        ) ||
                                match(roleAssignment, ROLE_ROUTE_DATA_EDIT, provider)
                );
    }

    private boolean match(RoleAssignment roleAssignment, String role, Provider provider) {
        return (
                role.equals(roleAssignment.getRole()) &&
                        provider.getChouetteInfo().xmlns.equals(roleAssignment.getOrganisation())
        );
    }


}
