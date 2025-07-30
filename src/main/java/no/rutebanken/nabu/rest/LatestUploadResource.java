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

package no.rutebanken.nabu.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static no.rutebanken.nabu.event.support.DateUtils.atDefaultZone;

@Component
@Produces("application/json")
@Path("latest_upload")
@Tags(value = {
        @Tag(name = "LatestUploadResource", description = "Latest upload resource")
})
public class LatestUploadResource {

    public static final Set<JobState> ERROR_JOB_STATES = Set.of(JobState.DUPLICATE, JobState.FAILED, JobState.TIMEOUT, JobState.CANCELLED);

    private final EventRepository eventRepository;
    private final ProviderRepository providerRepository;

    public LatestUploadResource(EventRepository eventRepository, ProviderRepository providerRepository) {
        this.eventRepository = eventRepository;
        this.providerRepository = providerRepository;
    }

    @GET
    @Path("/{providerId}")
    @Operation(summary = "Return the status of the latest dataset upload")
    @PreAuthorize("@authorizationService.canEditRouteData(#providerId)")
    public DataDeliveryStatus getLatestDataDeliveryStatus(@PathParam("providerId") Long providerId) {

        List<JobEvent> statusList = eventRepository.getLatestTimetableFileTransfer(providerId);


        return toDataDeliveryStatus(statusList);
    }


    @GET
    @Path("/{codespace}/{correlationId}")
    @Operation(summary = "Return the status of the delivery identified by its correlation id")
    @PreAuthorize("@authorizationService.canViewTimetableDataEvent(#codespace)")
    public DataDeliveryStatus getDataDeliveryStatus(@PathParam("codespace") String codespace, @PathParam("correlationId") String correlationId) {
        Provider provider = providerRepository.getProvider(codespace);
        if (provider == null) {
            throw new IllegalStateException("Provider not found");
        }
        List<JobEvent> events = eventRepository.getCorrelatedTimetableEvents(List.of(provider.getId(), provider.getChouetteInfo().migrateDataToProvider), correlationId);
        if (events.isEmpty()) {
            throw new NotFoundException("Correlation id not found");
        }
        JobEvent firstEvent = events.stream().min(Comparator.comparing(Event::getEventTime)).orElseThrow();
        return new DataDeliveryStatus(state(events), atDefaultZone(firstEvent.getEventTime()), firstEvent.getName());
    }


    protected DataDeliveryStatus toDataDeliveryStatus(Collection<JobEvent> statuses) {
        SortedSet<JobEvent> sortedEvents = new TreeSet<>(statuses);
        Instant latestDeliveryDate = null;
        DataDeliveryStatus.State state = null;
        String fileName = null;
        if (!sortedEvents.isEmpty()) {
            JobEvent firstEvent = sortedEvents.first();
            latestDeliveryDate = firstEvent.getEventTime();
            fileName = firstEvent.getName();
            state = state(sortedEvents);

        }

        return new DataDeliveryStatus(state, atDefaultZone(latestDeliveryDate), fileName);
    }

    /**
     * Return the state of the delivery given the current statuses.
     * Some steps in the import pipeline can run in parallel, thus the chronological order is arbitrary.
     * In particular, the OTP build graph event is not necessarily the last event recorded in the pipeline.
     */
    private static DataDeliveryStatus.State state(Collection<JobEvent> statuses) {
        if (statuses.stream().anyMatch(e -> TimeTableAction.OTP2_BUILD_GRAPH.toString().equals(e.getAction()) && JobState.OK.equals(e.getState()))) {
            return DataDeliveryStatus.State.OK;
        } else if (statuses.stream().anyMatch(e -> ERROR_JOB_STATES.contains(e.getState()))) {
            return DataDeliveryStatus.State.FAILED;
        } else {
            return DataDeliveryStatus.State.IN_PROGRESS;
        }
    }
}
