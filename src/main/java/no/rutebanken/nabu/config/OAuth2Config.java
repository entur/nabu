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

import org.entur.oauth2.AuthorizedWebClientBuilder;
import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.oauth2.MultiIssuerAuthenticationManagerResolver;
import org.entur.oauth2.RorAuth0RolesClaimAdapter;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configure Spring Beans for OAuth2 resource server and OAuth2 client security.
 */
@Configuration
public class OAuth2Config {

    private static final int MAX_DOWNLOAD_BUFFER_SIZE = 10 * 1024 * 1024;

    /**
     * Return a WebClient for authorized API calls.
     * The WebClient inserts a JWT bearer token in the Authorization HTTP header.
     * The JWT token is obtained from the configured Authorization Server.
     *
     * @param properties The spring.security.oauth2.client.registration.* properties that define  the OAuth2 clients.
     * @param audience   The API audience, required for obtaining a token from Auth0
     * @return a WebClient for authorized API calls.
     */
    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder, OAuth2ClientProperties properties, @Value("${nabu.oauth2.client.audience}") String audience) {

        // increase buffer size for downloading polygons from Baba
        webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(MAX_DOWNLOAD_BUFFER_SIZE))
                .build())
                .build();


        return new AuthorizedWebClientBuilder(webClientBuilder)
                .withOAuth2ClientProperties(properties)
                .withAudience(audience)
                .withClientRegistrationId("nabu")
                .build();
    }

    /**
     * Extract role assignments from a JWT token.
     *
     * @return
     */
    @Bean
    public RoleAssignmentExtractor roleAssignmentExtractor() {
        return new JwtRoleAssignmentExtractor();
    }

    /**
     * Adapt the JWT claims produced by the RoR Auth0 tenant to make them compatible with those produced by Keycloak.
     *
     * @param rorAuth0ClaimNamespace
     * @return
     */
    @Bean
    public RorAuth0RolesClaimAdapter rorAuth0RolesClaimAdapter(@Value("${nabu.oauth2.resourceserver.auth0.ror.claim.namespace}") String rorAuth0ClaimNamespace) {
        return new RorAuth0RolesClaimAdapter(rorAuth0ClaimNamespace);
    }

    /**
     * Identify the issuer of the JWT token (Auth0 or Keycloak) and forward the JWT token to the corresponding JWT decoder.
     * Verify that the audience is valid and adapt the JWT claim using the injected claim adapter.
     *
     * @param keycloakAudience
     * @param keycloakIssuer
     * @param keycloakJwksetUri
     * @param rorAuth0Audience
     * @param rorAuth0Issuer
     * @param rorAuth0RolesClaimAdapter
     * @return
     */
    @Bean
    public MultiIssuerAuthenticationManagerResolver multiIssuerAuthenticationManagerResolver(@Value("${nabu.oauth2.resourceserver.keycloak.jwt.audience}") String keycloakAudience,
                                                                                             @Value("${nabu.oauth2.resourceserver.keycloak.jwt.issuer-uri}") String keycloakIssuer,
                                                                                             @Value("${nabu.oauth2.resourceserver.keycloak.jwt.jwkset-uri}") String keycloakJwksetUri,
                                                                                             @Value("${nabu.oauth2.resourceserver.auth0.ror.jwt.audience}") String rorAuth0Audience,
                                                                                             @Value("${nabu.oauth2.resourceserver.auth0.ror.jwt.issuer-uri}") String rorAuth0Issuer,
                                                                                             RorAuth0RolesClaimAdapter rorAuth0RolesClaimAdapter) {
        return new MultiIssuerAuthenticationManagerResolver.Builder()
                .withKeycloakAudience(keycloakAudience)
                .withKeycloakIssuer(keycloakIssuer)
                .withKeycloakJwksetUri(keycloakJwksetUri)
                .withRorAuth0Audience(rorAuth0Audience)
                .withRorAuth0Issuer(rorAuth0Issuer)
                .withRorAuth0RolesClaimAdapter(rorAuth0RolesClaimAdapter)
                .build();

    }


}

