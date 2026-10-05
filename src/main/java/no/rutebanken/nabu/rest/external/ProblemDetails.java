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

package no.rutebanken.nabu.rest.external;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import no.rutebanken.nabu.rest.openapi.model.ProblemDetail;

/**
 * Builds the RFC 9457 error responses the progress endpoint declares.
 * <p>
 * Deliberately <em>not</em> a JAX-RS {@code ExceptionMapper}. A mapper is registered on the Jersey
 * application and would therefore also rewrite the errors of the status endpoint, which has been
 * published at version 1.0.0 answering {@code text/plain} and whose consumers did not ask for a new
 * format. Throwing a {@link WebApplicationException} that already carries its response keeps the
 * new format to the one operation that documents it; the day the status endpoint adopts problem
 * details too, a mapper becomes the simpler option and this class can go.
 */
final class ProblemDetails {

    static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

    /**
     * What a caller is told when the fault is ours. Deliberately uniform: every 5xx reads the same,
     * so no caller can tell two internal faults apart from the outside.
     */
    static final String INTERNAL_ERROR_DETAIL = "The request could not be completed because of an internal error.";

    private ProblemDetails() {
    }

    static WebApplicationException notFound(String detail) {
        return problem(Response.Status.NOT_FOUND, detail);
    }

    static WebApplicationException forbidden(String detail) {
        return problem(Response.Status.FORBIDDEN, detail);
    }

    /**
     * Carries no detail of its own. The exception behind a 5xx names provider ids and the state of
     * the stored events — the cross-tenant facts this endpoint exists to keep apart — so it is
     * logged and never repeated to the caller.
     */
    static WebApplicationException internalError() {
        return problem(Response.Status.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_DETAIL);
    }

    private static WebApplicationException problem(Response.Status status, String detail) {
        ProblemDetail problemDetail = new ProblemDetail(status.getReasonPhrase(), status.getStatusCode())
                .detail(detail);
        return new WebApplicationException(Response.status(status)
                                                   .type(APPLICATION_PROBLEM_JSON)
                                                   .entity(problemDetail)
                                                   .build());
    }
}
