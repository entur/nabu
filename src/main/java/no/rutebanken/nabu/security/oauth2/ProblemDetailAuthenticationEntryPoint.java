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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import no.rutebanken.nabu.rest.openapi.model.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Answers an unauthenticated request with RFC 9457 problem details, so that the 401 the progress
 * endpoint's OpenAPI contract declares is the 401 the service actually sends. It is wired for that
 * endpoint's path alone, in {@link NabuWebSecurityConfiguration}; everywhere else in this
 * application the 401 is unchanged.
 * <p>
 * The progress resource cannot do this itself the way it does its other error responses: Spring
 * Security rejects the request from inside the filter chain, which runs before the Jersey servlet,
 * so there is no resource method that was ever entered.
 * <p>
 * The challenge itself still comes from Spring Security. Delegating keeps the {@code
 * WWW-Authenticate} header, which is the specified part of a 401 and carries why the token was
 * rejected, and it keeps the status: a malformed request or an insufficient scope is a 400 or a 403
 * rather than a 401, and the body then reports what was actually sent instead of a 401 that was not.
 */
public class ProblemDetailAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint bearerTokenEntryPoint = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper objectMapper;

    public ProblemDetailAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        bearerTokenEntryPoint.commence(request, response, authException);

        // The delegate sets the status and the challenge header but writes nothing, so the body is
        // ours to add. The guard is for a future delegate that does commit: a second write would
        // throw on the error path, which is the worst place to raise a new failure.
        if (response.isCommitted()) {
            return;
        }

        int status = response.getStatus();
        HttpStatus resolved = HttpStatus.resolve(status);
        ProblemDetail problemDetail = new ProblemDetail(resolved != null ? resolved.getReasonPhrase() : "Error", status)
                .detail(detailFor(resolved));

        response.setContentType("application/problem+json");
        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }

    /**
     * The exception message names the token and what was wrong with it, which is a fact about the
     * credential rather than about the request, so the caller is told only that the credential did
     * not work. The specifics stay in {@code WWW-Authenticate}, where they are specified.
     * <p>
     * The three cases are the three the delegate can produce. They are worth telling apart: a
     * caller whose token is sound but too narrowly scoped would waste its time re-authenticating if
     * the body said its token was invalid.
     */
    private static String detailFor(HttpStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case BAD_REQUEST -> "The Authorization header is not a well-formed bearer token.";
            case FORBIDDEN -> "The access token does not carry the scope this request requires.";
            default -> "No or invalid access token.";
        };
    }
}
