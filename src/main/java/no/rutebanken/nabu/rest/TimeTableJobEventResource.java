package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.domain.JobStatus;
import no.rutebanken.nabu.rest.domain.JobStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static no.rutebanken.nabu.rest.mapper.EnumMapper.convertEnums;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_ADMIN;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_EDIT;


@Component
@Produces("application/json")
@Path("/jobs")
public class TimeTableJobEventResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    private static final String STATUS_JOB_TYPE = JobEvent.JobDomain.TIMETABLE.name();

    @GET
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public List<JobStatus> listStatus(@PathParam("providerId") Long providerId, @QueryParam("from") Date from,
                                             @QueryParam("to") Date to, @QueryParam("action") List<String> actions,
                                             @QueryParam("state") List<JobStatus.State> states, @QueryParam("chouetteJobId") List<Long> jobIds,
                                             @QueryParam("fileName") List<String> fileNames) {

        if (providerId==null){
            logger.debug("Returning status for all providers");
        } else {
            logger.debug("Returning status for provider with id '" + providerId + "'");
        }

        Instant instantFrom = from == null ? null : from.toInstant();
        Instant instantTo = to == null ? null : to.toInstant();

        List<String> externalIds = jobIds == null ? null : jobIds.stream().map(jobId -> jobId.toString()).collect(Collectors.toList());

        try {
            List<JobEvent> eventsForProvider = eventService.findTimetableJobEvents(providerId, instantFrom, instantTo,
                    actions, convertEnums(states, JobState.class), externalIds, fileNames);
            return convert(eventsForProvider);
        } catch (Exception e) {
            logger.error("Erring fetching status for provider with id " + providerId + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @GET
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "')")
    public List<JobStatus> listStatus(@QueryParam("from") Date from,
                                             @QueryParam("to") Date to, @QueryParam("action") List<String> actions,
                                             @QueryParam("state") List<JobStatus.State> states, @QueryParam("chouetteJobId") List<Long> jobIds,
                                             @QueryParam("fileName") List<String> fileNames) {
        return listStatus(null, from, to, actions, states, jobIds, fileNames);
    }

    @DELETE
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "')")
    public void clearAllStatus() {
        eventService.clearAll(STATUS_JOB_TYPE);
    }

    @DELETE
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "')")
    public void clearStatusForProvider(@PathParam("providerId") Long providerId) {
        eventService.clear(STATUS_JOB_TYPE, providerId);
    }

    public List<JobStatus> convert(List<JobEvent> statusForProvider) {
        List<JobStatus> list = new ArrayList<>();
        // Map from internal Status object to Rest service JobStatusEvent object
        String correlationId = null;
        JobStatus currentAggregation = null;
        for (JobEvent in : statusForProvider) {

            if (!in.getCorrelationId().equals(correlationId)) {

                correlationId = in.getCorrelationId();

                // Create new Aggregation
                currentAggregation = new JobStatus();
                currentAggregation.setFirstEvent(Date.from(in.getEventTime()));
                currentAggregation.setFileName(in.getName());
                currentAggregation.setCorrelationId(in.getCorrelationId());
                currentAggregation.setProviderId(in.getProviderId());

                list.add(currentAggregation);
            }


            currentAggregation.addEvent(JobStatusEvent.createFromJobEvent(in));
        }

        for (JobStatus agg : list) {
            JobStatusEvent event = agg.getEvents().get(agg.getEvents().size() - 1);
            agg.setLastEvent(event.date);
            agg.setEndStatus(event.state);
            long durationMillis = agg.getLastEvent().getTime() - agg.getFirstEvent().getTime();
            agg.setDurationMillis(durationMillis);
        }

        Collections.sort(list, (o1, o2) -> o1.getFirstEvent().compareTo(o2.getFirstEvent()));

        return list;
    }

}
