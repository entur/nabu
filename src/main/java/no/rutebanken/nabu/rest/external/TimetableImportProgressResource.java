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

import jakarta.ws.rs.NotFoundException;
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
import org.springframework.security.access.AccessDeniedException;
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

    @Override
    public ImportProgress getImportProgress(String correlationId) {
        logger.debug("Returning import progress for correlation id '{}'", correlationId);

        List<JobEvent> events = eventRepository.getCorrelatedTimetableEvents(correlationId);
        if (events.isEmpty()) {
            throw new NotFoundException("Correlation id not found");
        }

        // Both failures resolveProvider can raise name the correlation id and the provider in their
        // message, and the servlet container logs an unmapped exception with its root cause, so
        // catching to log here would only duplicate the stack trace.
        Provider provider = resolveProvider(events, correlationId);
        String codespace = provider.getChouetteInfo().xmlns;

        // Runs here rather than in a @PreAuthorize because the codespace is resolved from the events
        // rather than bound from a path parameter. Same predicate as the status endpoint's
        // canViewTimetableDataEvent, which delegates to canEditRouteData once it has resolved the
        // provider we already hold.
        if (!authorizationService.canEditRouteData(provider.getId())) {
            throw new AccessDeniedException("Insufficient privileges for correlation id " + correlationId);
        }

        return ImportProgressMapper.toImportProgress(codespace, correlationId, events);
    }

    /**
     * Resolve the provider that owns the import from its earliest event. The events of one
     * correlation id need not all carry the same provider — an import that crosses into a migration
     * target dataspace records the target on its later events — so the earliest one decides, which
     * is the provider the data was delivered to.
     */
    private Provider resolveProvider(List<JobEvent> events, String correlationId) {
        Long providerId = events.stream()
                .filter(event -> event.getProviderId() != null)
                .min(JobEventAggregation.BY_EVENT_TIME)
                .map(JobEvent::getProviderId)
                .orElseThrow(() -> new IllegalStateException("No event for correlation id " + correlationId + " carries a provider id"));

        Provider provider = providerRepository.getProvider(providerId);
        if (provider == null || provider.getChouetteInfo() == null || provider.getChouetteInfo().xmlns == null) {
            throw new IllegalStateException("Provider " + providerId + " for correlation id " + correlationId + " does not resolve to a codespace");
        }
        return provider;
    }
}
