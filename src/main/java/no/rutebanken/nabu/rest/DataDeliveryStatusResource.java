package no.rutebanken.nabu.rest;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.*;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_ADMIN;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_EDIT;

@Component
@Produces("application/json")
@Path("/dataDelivery")
public class DataDeliveryStatusResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EventRepository eventRepository;

    @GET
    @Path("/{providerId}/latest")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public DataDeliveryStatus getLatestDataDeliveryStatus(@PathParam("providerId") Long providerId) {

        List<JobEvent> statusList = eventRepository.getLatestDeliveryStatusForProvider(JobEvent.JobDomain.TIMETABLE.toString(), providerId);


        return toDataDeliveryStatus(statusList);
    }

    protected DataDeliveryStatus toDataDeliveryStatus(Collection<JobEvent> statuses) {
        SortedSet<JobEvent> sortedEvents = new TreeSet<>(statuses);
        Date latestDeliveryDate = null;
        DataDeliveryStatus.State state = null;
        if (!sortedEvents.isEmpty()) {

            latestDeliveryDate = Date.from(sortedEvents.first().getEventTime());
            if (sortedEvents.stream().anyMatch(e -> TimeTableAction.BUILD_GRAPH.toString().equals(e.getAction()) && JobState.OK.equals(e.getState()))) {
                state = DataDeliveryStatus.State.OK;
            } else if (sortedEvents.stream().anyMatch(e -> Sets.newHashSet(JobState.DUPLICATE, JobState.FAILED, JobState.TIMEOUT).contains(e.getState()))) {
                state = DataDeliveryStatus.State.FAILED;
            } else {
                state = DataDeliveryStatus.State.IN_PROGRESS;
            }

        }
        
        return new DataDeliveryStatus(state, latestDeliveryDate);
    }
}
