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
import no.rutebanken.nabu.domain.event.CrudEventSearch;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.ApiCrudEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.List;

@Component
@Produces("application/json")
@Path("change_log")
@Tags(value = {
        @Tag(name = "ChangeLogResource", description ="Change log resource")
})
@PreAuthorize("@authorizationService.isRouteDataAdmin() or @authorizationService.isOrganisationAdmin()")
public class ChangeLogResource {

    private final EventRepository eventRepository;

    public ChangeLogResource(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GET
    @Operation(summary = "Return the list of CRUD event for the given search parameters")
    public List<ApiCrudEvent> find(@BeanParam CrudEventSearch search) {
        return eventRepository.findCrudEvents(search).stream().map(ApiCrudEvent::fromCrudEvent).toList();
    }
}
