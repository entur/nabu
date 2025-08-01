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

package no.rutebanken.nabu.rest.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
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

    private final EventRepository eventRepository;

    public LatestUploadResource(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GET
    @Path("/{providerId}")
    @Operation(summary = "Return the status of the latest dataset upload")
    @PreAuthorize("@authorizationService.canEditRouteData(#providerId)")
    public DataDeliveryStatus getLatestDataDeliveryStatus(@PathParam("providerId") Long providerId) {
        List<JobEvent> statusList = eventRepository.getLatestTimetableFileTransfer(providerId);
        return toDataDeliveryStatus(statusList);
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
            state = DataDeliveryStatus.State.of(sortedEvents);

        }

        return new DataDeliveryStatus(state, atDefaultZone(latestDeliveryDate), fileName);
    }

}
