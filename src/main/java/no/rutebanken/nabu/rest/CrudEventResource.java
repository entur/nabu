package no.rutebanken.nabu.rest;

import io.swagger.annotations.Api;
import no.rutebanken.nabu.domain.event.CrudEventSearch;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.ApiCrudEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Produces("application/json")
@Path("/crud_events")
@Api(tags = {"Crud event resource"}, produces = "application/json")
public class CrudEventResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventRepository eventRepository;

    @GET
    public List<ApiCrudEvent> find(@BeanParam CrudEventSearch search) {
        return eventRepository.findCrudEvents(search).stream().map(crudEvent -> ApiCrudEvent.fromCrudEvent(crudEvent)).collect(Collectors.toList());
    }
}
