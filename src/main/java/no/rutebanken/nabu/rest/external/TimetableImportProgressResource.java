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
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.event.aggregation.JobEventAggregation;
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.openapi.api.ProgressApi;
import no.rutebanken.nabu.rest.openapi.model.ImportProgress;
import org.rutebanken.helper.organisation.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Per-stage progress of one timetable data delivery, keyed by correlation id alone. The codespace is
 * resolved from the events rather than supplied by the caller, so the caller cannot choose the
 * subject of the authorization check.
 */
@Component
public class TimetableImportProgressResource implements ProgressApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventRepository eventRepository;
    private final ProviderRepository providerRepository;
    private final AuthorizationService<Long> authorizationService;

    public TimetableImportProgressResource(EventRepository eventRepository,
                                           ProviderRepository providerRepository,
                                           AuthorizationService<Long> authorizationService) {
        this.eventRepository = eventRepository;
        this.providerRepository = providerRepository;
        this.authorizationService = authorizationService;
    }

    /**
     * Every failure leaves here as a {@link jakarta.ws.rs.WebApplicationException} carrying a
     * problem+json response, which is what the OpenAPI contract declares for this operation. The
     * catch-all is what makes that true of a fault nobody anticipated as well as of the two this
     * method raises on purpose, and it is confined to this method so that the status endpoint
     * sharing the Jersey application keeps answering exactly as it does today.
     */
    @Override
    public ImportProgress getImportProgress(String correlationId) {
        logger.debug("Returning import progress for correlation id '{}'", correlationId);

        try {
            return importProgress(correlationId);
        } catch (WebApplicationException alreadyAProblem) {
            throw alreadyAProblem;
        } catch (RuntimeException e) {
            // Nothing below logs, because every message worth logging is one that must not be
            // returned: they name provider ids and the state of the stored events.
            logger.error("Could not return import progress for correlation id '{}'", correlationId, e);
            throw ProblemDetails.internalError();
        }
    }

    private ImportProgress importProgress(String correlationId) {
        List<JobEvent> events = eventRepository.getCorrelatedTimetableEvents(correlationId);
        if (events.isEmpty()) {
            throw ProblemDetails.notFound("Correlation id not found");
        }

        Provider provider = resolveProvider(events, correlationId);
        String codespace = provider.getChouetteInfo().xmlns;

        // Runs here rather than in a @PreAuthorize because the codespace is resolved from the events
        // rather than bound from a path parameter. Same predicate as the status endpoint's
        // canViewTimetableDataEvent, which delegates to canEditRouteData once it has resolved the
        // provider we already hold.
        if (!authorizationService.canEditRouteData(provider.getId())) {
            // Names the correlation id, which the caller sent, and not the codespace, which it is
            // being refused.
            throw ProblemDetails.forbidden("Insufficient privileges for correlation id " + correlationId);
        }

        return ImportProgressMapper.toImportProgress(codespace, correlationId, events);
    }

    /**
     * Resolve the provider that owns the import from its earliest event. The events of one
     * correlation id need not all carry the same provider — an import that crosses into a migration
     * target dataspace records the target on its later events — so the earliest one decides, which
     * is the provider the data was delivered to.
     * <p>
     * The earliest event must name that provider itself. Every timetable event marduk publishes
     * carries one, so an import whose first event does not is inconsistent, and falling through to a
     * later event would authorize against a provider the delivery was not made to.
     */
    private Provider resolveProvider(List<JobEvent> events, String correlationId) {
        // The caller has already rejected an empty list.
        JobEvent earliest = events.stream().min(JobEventAggregation.BY_EVENT_TIME).orElseThrow();

        Long providerId = earliest.getProviderId();
        if (providerId == null) {
            throw new IllegalStateException("The earliest event for correlation id " + correlationId + " carries no provider id");
        }

        Provider provider = providerRepository.getProvider(providerId);
        if (provider == null || provider.getChouetteInfo() == null || provider.getChouetteInfo().xmlns == null) {
            throw new IllegalStateException("Provider " + providerId + " for correlation id " + correlationId + " does not resolve to a codespace");
        }
        return provider;
    }
}
