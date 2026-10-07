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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;

import static no.rutebanken.nabu.security.oauth2.NabuWebSecurityConfiguration.PROGRESS_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The matcher that decides which requests get the new problem+json 401 and which keep the empty one
 * they have always had. Nothing else in this application separates the two, so a pattern that
 * reached one path too far would change an endpoint that was never meant to be touched — silently,
 * and only for callers who failed to authenticate.
 */
class ProgressEndpointMatcherTest {

    private static boolean matches(String uri) {
        return PROGRESS_ENDPOINT.matches(new MockHttpServletRequest("GET", uri));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/services/events-external/progress/9f3c1b2e-7a44-4f10-9c1d-2b8e5a6f0d13",
            "/services/events-external/progress/corr-with-hyphens",
            "/services/events-external/progress/"
    })
    void theProgressEndpointIsMatched(String uri) {
        assertTrue(matches(uri), uri + " is the endpoint whose contract declares a 401 body");
    }

    /**
     * The published status endpoint shares the external Jersey application and sits one path
     * segment away, which makes it the likeliest thing to catch by accident.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "/services/events-external/status/RUT/9f3c1b2e-7a44-4f10-9c1d-2b8e5a6f0d13",
            "/services/events-external/openapi.json",
            "/services/events/timetable_job_events",
            "/services/events/admin_summary/status/aggregation",
            "/services/events/notifications",
            "/actuator/health",
            "/actuator/prometheus"
    })
    void everyOlderEndpointIsLeftAlone(String uri) {
        assertFalse(matches(uri), uri + " predates problem+json and must keep the 401 it has today");
    }

    /**
     * A prefix match would be wrong: these are different endpoints that merely start with the same
     * letters, and the pattern is anchored on the path segment rather than on the string.
     */
    @Test
    void aPathThatMerelyStartsLikeTheProgressEndpointIsNotMatched() {
        assertFalse(matches("/services/events-external/progressive/abc"));
        assertFalse(matches("/services/events-external/progress-report/abc"));
    }

    /**
     * There is no progress resource outside the external application. A request to the internal one
     * is a 404 either way, but it must not be a 404 dressed in the external contract's format.
     */
    @Test
    void theSamePathUnderTheInternalApplicationIsNotMatched() {
        assertFalse(matches("/services/events/progress/abc"));
    }
}
