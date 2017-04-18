package no.rutebanken.nabu.rest;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.repository.StatusRepository;
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
    StatusRepository statusRepository;

    @GET
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public DataDeliveryStatus getLatestDataDeliveryStatus(@PathParam("providerId") Long providerId) {

        List<Status> statusList = statusRepository.getLatestDeliveryStatusForProvider(providerId);


        return toDataDeliveryStatus(statusList);
    }

    protected DataDeliveryStatus toDataDeliveryStatus(Collection<Status> statuses) {
        SortedSet<Status> sortedStatuses = new TreeSet<>(statuses);
        Date latestDeliveryDate = null;
        DataDeliveryStatus.State state = null;
        if (!sortedStatuses.isEmpty()) {
            latestDeliveryDate = sortedStatuses.first().date;
            if (sortedStatuses.stream().anyMatch(s -> Status.Action.BUILD_GRAPH.equals(s.action) && Status.State.OK.equals(s.state))) {
                state = DataDeliveryStatus.State.OK;
            } else if (sortedStatuses.stream().anyMatch(s -> Sets.newHashSet(Status.State.DUPLICATE, Status.State.FAILED, Status.State.TIMEOUT).contains(s.state))) {
                state = DataDeliveryStatus.State.FAILED;
            } else {
                state = DataDeliveryStatus.State.IN_PROGRESS;
            }
        }

        return new DataDeliveryStatus(state, latestDeliveryDate);
    }
}
