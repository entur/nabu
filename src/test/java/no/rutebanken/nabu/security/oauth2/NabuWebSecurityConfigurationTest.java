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
 */

package no.rutebanken.nabu.security.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the real security configuration, which no other test reaches: the integration tests run
 * under the {@code test} profile, where {@link NabuWebSecurityConfiguration} is excluded and every
 * request is permitted.
 * <p>
 * The reason it exists is narrower than "the 401 works". Giving the progress endpoint the error body
 * its contract declares meant supplying an authentication entry point, and there is only one filter
 * chain in this application, so that one object answers for every endpoint nabu serves. These tests
 * pin the consequence: the new body appears on the progress endpoint and on nothing else.
 */
@SpringJUnitWebConfig(classes = {NabuWebSecurityConfiguration.class, NabuWebSecurityConfigurationTest.StubbedIssuers.class})
class NabuWebSecurityConfigurationTest {

    /**
     * Carries no stereotype annotation on purpose. Naming it in {@code classes} is enough for
     * Spring to register its {@code @Bean} methods here, while leaving it invisible to component
     * scanning — and these stubs must stay invisible, because every other test in this module builds
     * its context by scanning this package. A stubbed {@code ClientRegistrationRepository} reaching
     * those contexts switches off OAuth2 client auto-configuration and takes the whole suite down.
     * <p>
     * {@code @TestConfiguration} would not be enough: it is excluded from scanning by the
     * {@code TypeExcludeFilter} that {@code @SpringBootApplication} contributes, and
     * {@code NabuTestApp} declares its own {@code @ComponentScan}, which replaces that default.
     */
    static class StubbedIssuers {

        /**
         * Never consulted: these requests carry no token, so nothing reaches the point of resolving
         * an issuer for one.
         */
        @Bean
        MultiIssuerAuthenticationManagerResolver multiIssuerAuthenticationManagerResolver() {
            return Mockito.mock(MultiIssuerAuthenticationManagerResolver.class);
        }

        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return registrationId -> null;
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    /**
     * Captured by running these same assertions in a worktree at the commit before problem details
     * were introduced, so it is the challenge every endpoint of this application was already
     * sending. Pinned in full rather than by prefix because the parameter is the part most easily
     * lost: supplying an entry point of our own replaces the one Spring Security picks by default,
     * and dropping a challenge parameter would be invisible to every test that only checks a status
     * code. If a Spring Security upgrade changes this string, that is a finding about our callers
     * and not a flaky test.
     */
    private static final String BEARER_CHALLENGE =
            "Bearer resource_metadata=\"http://localhost/.well-known/oauth-protected-resource\"";

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockHttpServletResponse withoutAToken(String uri) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        springSecurityFilterChain.doFilter(request, response, new MockFilterChain());
        return response;
    }

    /**
     * The whole point of the scoping. Every one of these endpoints shipped before problem details
     * existed in this application, and a caller that fails to authenticate against them must get
     * back exactly what it got back yesterday: a status, a challenge, and no body at all.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "/services/events-external/status/RUT/9f3c1b2e-7a44-4f10-9c1d-2b8e5a6f0d13",
            "/services/events/timetable_job_events",
            "/services/events/admin_summary/status/aggregation",
            "/services/events/notifications"
    })
    void anOlderEndpointStillAnswersAnUnauthenticatedRequestWithAnEmptyBody(String uri) throws Exception {
        MockHttpServletResponse response = withoutAToken(uri);

        assertEquals(401, response.getStatus());
        assertEquals("", response.getContentAsString(), uri + " must not have gained a response body");
        assertEquals(null, response.getContentType(), uri + " must not have gained a content type");
        assertEquals(BEARER_CHALLENGE, challengeOf(response), uri + " must keep the challenge it had");
    }

    @Test
    void theProgressEndpointAnswersAnUnauthenticatedRequestWithProblemDetails() throws Exception {
        MockHttpServletResponse response = withoutAToken("/services/events-external/progress/some-correlation-id");

        assertEquals(401, response.getStatus());
        assertTrue(MediaType.APPLICATION_PROBLEM_JSON.isCompatibleWith(MediaType.parseMediaType(response.getContentType())),
                "content type was: " + response.getContentType());

        JsonNode problem = objectMapper.readTree(response.getContentAsString());
        assertEquals("Unauthorized", problem.get("title").asText());
        assertEquals(401, problem.get("status").asInt());
        assertTrue(problem.hasNonNull("detail"));

        // The body is additional to the challenge, not instead of it: this endpoint sends the same
        // one as every other.
        assertEquals(BEARER_CHALLENGE, challengeOf(response));
    }

    /**
     * The endpoints deliberately left open stay open. Worth asserting next to the rest: the entry
     * point is reached by whatever the authorization rules reject, so a mistake there would show up
     * as a 401 on a liveness probe rather than as anything resembling an authentication bug.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "/actuator/health",
            "/actuator/health/liveness",
            "/actuator/health/readiness",
            "/actuator/prometheus",
            "/services/events/openapi.json",
            "/services/events-external/openapi.json"
    })
    void anUnauthenticatedEndpointIsNotChallengedAtAll(String uri) throws Exception {
        assertEquals(200, withoutAToken(uri).getStatus(), uri + " is served without a token");
    }

    private static String challengeOf(MockHttpServletResponse response) {
        return String.valueOf(response.getHeader(HttpHeaders.WWW_AUTHENTICATE));
    }
}
