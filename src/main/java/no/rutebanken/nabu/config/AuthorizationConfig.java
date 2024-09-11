/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.nabu.config;

import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.security.permissionstore.PermissionStoreClient;
import no.rutebanken.nabu.security.permissionstore.PermissionStoreRestRoleAssignmentExtractor;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.authorization.AuthorizationService;
import org.rutebanken.helper.organisation.authorization.DefaultAuthorizationService;
import org.rutebanken.helper.organisation.authorization.FullAccessAuthorizationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Configure authorization.
 */
@Configuration
public class AuthorizationConfig {

    @Bean
    public RoleAssignmentExtractor roleAssignmentExtractor(
            PermissionStoreClient permissionStoreClient
    ) {
        return new PermissionStoreRestRoleAssignmentExtractor(
                permissionStoreClient,
                "nabu",
                Set.of(AuthorizationConstants.ROLE_ROUTE_DATA_EDIT,
                        AuthorizationConstants.ROLE_ROUTE_DATA_ADMIN,
                        AuthorizationConstants.ROLE_ORGANISATION_EDIT)
        );
    }

    @ConditionalOnProperty(
            value = "nabu.security.authorization-service",
            havingValue = "token-based"
    )
    @Bean("authorizationService")
    public AuthorizationService<Long> tokenBasedAuthorizationService(ProviderRepository providerRepository, RoleAssignmentExtractor roleAssignmentExtractor) {
        return new DefaultAuthorizationService<>(
                providerId -> providerRepository.getProvider(providerId) == null ? null : providerRepository.getProvider(providerId).getChouetteInfo().xmlns,
                roleAssignmentExtractor
        );
    }

    @ConditionalOnProperty(
            value = "nabu.security.authorization-service",
            havingValue = "full-access"
    )
    @Bean("authorizationService")
    public AuthorizationService<Long> fullAccessAuthorizationService() {
        return new FullAccessAuthorizationService();
    }

}


