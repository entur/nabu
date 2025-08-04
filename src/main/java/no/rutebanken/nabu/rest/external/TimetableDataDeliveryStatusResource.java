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
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static no.rutebanken.nabu.event.support.DateUtils.atDefaultZone;

@Component
@Produces("application/json")
@Path("status")
@Tags(value = {
        @Tag(name = "TimetableDataDeliveryStatus", description = "Status of a timetable data delivery")
})
public class TimetableDataDeliveryStatusResource {

    private final EventRepository eventRepository;
    private final ProviderRepository providerRepository;

    public TimetableDataDeliveryStatusResource(EventRepository eventRepository, ProviderRepository providerRepository) {
        this.eventRepository = eventRepository;
        this.providerRepository = providerRepository;
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
        return new DataDeliveryStatus(DataDeliveryStatus.State.of(events), atDefaultZone(firstEvent.getEventTime()), firstEvent.getName());
    }

}
