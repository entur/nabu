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

import io.swagger.annotations.Api;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.rest.domain.JobStatus;
import no.rutebanken.nabu.rest.domain.JobStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
@Path("timetable")
@Api(tags = {"Timetable job event resource"}, produces = "application/json")
public class TimeTableJobEventResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    @Autowired
    private ProviderRepository providerRepository;

    private static final String STATUS_JOB_TYPE = JobEvent.JobDomain.TIMETABLE.name();

    @GET
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public List<JobStatus> listStatus(@PathParam("providerId") Long providerId, @QueryParam("from") Date from,
                                             @QueryParam("to") Date to, @QueryParam("action") List<String> actions,
                                             @QueryParam("state") List<JobStatus.State> states, @QueryParam("chouetteJobId") List<Long> jobIds,
                                             @QueryParam("fileName") List<String> fileNames) {

        if (providerId == null) {
            logger.debug("Returning status for all providers");
        } else {
            logger.debug("Returning status for provider with id '" + providerId + "'");
        }

        Instant instantFrom = from == null ? null : from.toInstant();
        Instant instantTo = to == null ? null : to.toInstant();

        List<String> externalIds = jobIds == null ? null : jobIds.stream().map(jobId -> jobId.toString()).collect(Collectors.toList());
        List<Long> relatedProviderIds = mapToAllRelatedProviderIds(providerId);
        try {
            List<JobEvent> eventsForProvider = eventService.findTimetableJobEvents(relatedProviderIds, instantFrom, instantTo,
                    actions, convertEnums(states, JobState.class), externalIds, fileNames);
            return convert(eventsForProvider);
        } catch (Exception e) {
            logger.error("Erring fetching status for provider with id " + providerId + ": " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Return all ids for providers related to a given provider, that is the provider it self + either the provider that it migrates to or the provider that migrates to it.
     */
    private List<Long> mapToAllRelatedProviderIds(Long providerId) {
        if (providerId == null) {
            return new ArrayList<>();
        }
        List<Long> relatedProviderIds = providerRepository.getProviders().stream().filter(provider -> providerId.equals(provider.chouetteInfo.migrateDataToProvider))
                                                .map(provider -> provider.id).collect(Collectors.toList());
        relatedProviderIds.add(providerId);
        Provider provider = providerRepository.getProvider(providerId);
        if (provider != null && provider.chouetteInfo.migrateDataToProvider != null) {
            relatedProviderIds.add(provider.chouetteInfo.migrateDataToProvider);
        }

        return relatedProviderIds;
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
        mapToAllRelatedProviderIds(providerId).stream().forEach(clearProviderId -> eventService.clear(STATUS_JOB_TYPE, clearProviderId));
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
