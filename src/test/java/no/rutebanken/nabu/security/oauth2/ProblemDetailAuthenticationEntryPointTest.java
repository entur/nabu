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
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The 401 is the one response in the external contract that no resource method ever produces, so it
 * is the one most easily left behind when the contract changes. These tests cover it directly, since
 * the integration tests run with security disabled and can never reach it.
 */
class ProblemDetailAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockHttpServletResponse commence() throws Exception {
        return commence(new InsufficientAuthenticationException("Full authentication is required to access this resource"));
    }

    private MockHttpServletResponse commence(AuthenticationException authException) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new ProblemDetailAuthenticationEntryPoint(objectMapper).commence(
                new MockHttpServletRequest("GET", "/services/events-external/progress/some-correlation-id"),
                response,
                authException);
        return response;
    }

    @Test
    void anUnauthenticatedRequestIsAnsweredWithProblemDetails() throws Exception {
        MockHttpServletResponse response = commence();

        assertEquals(401, response.getStatus());
        assertTrue(MediaType.APPLICATION_PROBLEM_JSON.isCompatibleWith(MediaType.parseMediaType(response.getContentType())),
                "the contract declares application/problem+json, content type was: " + response.getContentType());

        JsonNode problem = objectMapper.readTree(response.getContentAsString());
        assertEquals("Unauthorized", problem.get("title").asText());
        assertEquals(401, problem.get("status").asInt());
        assertTrue(problem.hasNonNull("detail"));
    }

    /**
     * The challenge is the specified part of a 401 and the only machine-readable one. Adding a body
     * must not cost it, which is why the entry point delegates rather than writing the status itself.
     * Only the scheme is asserted: Spring Security decorates the challenge with parameters of its
     * own, and pinning the whole header would fail on an upgrade that is none of our business.
     */
    @Test
    void theBearerChallengeSurvivesTheAddedBody() throws Exception {
        String challenge = commence().getHeader(HttpHeaders.WWW_AUTHENTICATE);

        assertTrue(challenge != null && challenge.startsWith("Bearer"),
                "the delegate's challenge must survive, header was: " + challenge);
    }

    /**
     * The contract declares 401, 403, 404 and 500. Spring Security answers an unreadable bearer
     * token with a 400, which is none of them, so it is answered as the 401 it amounts to — with
     * the reason left in the challenge, where it is specified.
     */
    @Test
    void anUnreadableBearerTokenIsAnsweredAsAnInvalidOne() throws Exception {
        MockHttpServletResponse response = commence(new OAuth2AuthenticationException(
                BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request")));

        assertEquals(401, response.getStatus());

        JsonNode problem = objectMapper.readTree(response.getContentAsString());
        assertEquals("Unauthorized", problem.get("title").asText());
        assertEquals(401, problem.get("status").asInt());

        assertTrue(String.valueOf(response.getHeader(HttpHeaders.WWW_AUTHENTICATE)).contains("invalid_request"),
                "the error code must survive in the challenge");
    }

    /**
     * Why the token was rejected belongs in WWW-Authenticate, not in a body a caller might log or
     * display. Pinned because the obvious implementation — passing the exception message through as
     * {@code detail} — looks more helpful and is how the leak would get introduced.
     */
    @Test
    void theRejectionReasonDoesNotReachTheBody() throws Exception {
        String body = commence().getContentAsString();

        assertFalse(body.contains("Full authentication is required"),
                "the authentication exception message must not be echoed, body was: " + body);
    }
}
