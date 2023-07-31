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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import no.rutebanken.nabu.domain.event.CrudEventSearch;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.ApiCrudEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_ADMIN;

@Component
@Produces("application/json")
@Path("change_log")
@Tags(value = {
        @Tag(name = "ChangeLogResource", description ="Change log resource")
})
@PreAuthorize("hasAnyRole('" + ROLE_ROUTE_DATA_ADMIN + "','" + ROLE_ORGANISATION_EDIT + "')")
public class ChangeLogResource {

    @Autowired
    private EventRepository eventRepository;

    @GET
    @Operation(summary = "Return the list of CRUD event for the given search parameters")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ApiCrudEvent.class)))})})
    public List<ApiCrudEvent> find(@BeanParam CrudEventSearch search) {
        return eventRepository.findCrudEvents(search).stream().map(ApiCrudEvent::fromCrudEvent).toList();
    }
}
