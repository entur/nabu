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

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static no.rutebanken.nabu.rest.mapper.EnumMapper.convertEnums;


@Component
@Produces("application/json")
@Path("timetable")
@Tags(value = {
        @Tag(name = "TimeTableJobEventResource", description ="Timetable job event resource")
})
public class TimeTableJobEventResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    @Autowired
    private ProviderRepository providerRepository;

    private static final String STATUS_JOB_TYPE = JobEvent.JobDomain.TIMETABLE.name();

    @GET
    @Path("/{providerId}")
    @PreAuthorize("@userContextService.canEditRouteData(#providerId)")



    @Operation(summary = "Return the import for the given search parameters")
    public List<JobStatus> listStatus(@PathParam("providerId") Long providerId, @QueryParam("from") Date from,
                                             @QueryParam("to") Date to, @QueryParam("action") List<String> actions,
                                             @QueryParam("state") List<JobStatus.State> states, @QueryParam("chouetteJobId") List<Long> jobIds,
                                             @QueryParam("fileName") List<String> fileNames) {

        if (providerId == null) {
            logger.debug("Returning status for all providers");
        } else {
            logger.debug("Returning status for provider with id '{}'", providerId);
        }

        Instant instantFrom = from == null ? null : from.toInstant();
        Instant instantTo = to == null ? null : to.toInstant();

        List<String> externalIds = jobIds == null ? null : jobIds.stream().map(Object::toString).toList();
        List<Long> relatedProviderIds = mapToAllRelatedProviderIds(providerId);
        try {
            List<JobEvent> eventsForProvider = eventService.findTimetableJobEvents(relatedProviderIds, instantFrom, instantTo,
                    actions, convertEnums(states, JobState.class), externalIds, fileNames);
            return convert(eventsForProvider);
        } catch (Exception e) {
            logger.error("Erring fetching status for provider with id {}: {}", providerId, e.getMessage(), e);
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
    @PreAuthorize("@userContextService.isRouteDataAdmin()")
    public List<JobStatus> listStatus(@QueryParam("from") Date from,
                                             @QueryParam("to") Date to, @QueryParam("action") List<String> actions,
                                             @QueryParam("state") List<JobStatus.State> states, @QueryParam("chouetteJobId") List<Long> jobIds,
                                             @QueryParam("fileName") List<String> fileNames) {
        return listStatus(null, from, to, actions, states, jobIds, fileNames);
    }

    @DELETE
    @PreAuthorize("@userContextService.isRouteDataAdmin()")
    public void clearAllStatus() {
        eventService.clearAll(STATUS_JOB_TYPE);
    }

    @DELETE
    @Path("/{providerId}")
    @PreAuthorize("@userContextService.isRouteDataAdmin()")
    public void clearStatusForProvider(@PathParam("providerId") Long providerId) {
        mapToAllRelatedProviderIds(providerId).forEach(clearProviderId -> eventService.clear(STATUS_JOB_TYPE, clearProviderId));
    }

    public List<JobStatus> convert(List<JobEvent> statusForProvider) {
        List<JobStatus> list = new ArrayList<>();
        // Map from internal Status object to Rest service JobStatusEvent object
        String correlationId = null;
        JobStatus currentAggregation = null;

        List<JobEvent> sortedStatusForProvider = statusForProvider.stream().sorted(Comparator.comparing(JobEvent::getCorrelationId).thenComparing(JobEvent::getEventTime)).toList();

        for (JobEvent in : sortedStatusForProvider) {

            if (!in.getCorrelationId().equals(correlationId)) {

                correlationId = in.getCorrelationId();

                // Create new Aggregation
                currentAggregation = new JobStatus();
                currentAggregation.setFirstEvent(Date.from(in.getEventTime()));
                currentAggregation.setFileName(in.getName());
                currentAggregation.setCorrelationId(in.getCorrelationId());
                currentAggregation.setProviderId(in.getProviderId());
                currentAggregation.setUsername(in.getUsername());

                list.add(currentAggregation);
            }

            // errorCode overwritten when processing each event so that only the error code for the last event is sent.
            currentAggregation.setErrorCode(in.getErrorCode());
            currentAggregation.addEvent(JobStatusEvent.createFromJobEvent(in));
        }

        for (JobStatus agg : list) {
            JobStatusEvent event = agg.getEvents().getLast();
            agg.setLastEvent(event.date);
            agg.setEndStatus(event.state);
            long durationMillis = agg.getLastEvent().getTime() - agg.getFirstEvent().getTime();
            agg.setDurationMillis(durationMillis);
        }

        list.sort(Comparator.comparing(JobStatus::getFirstEvent));

        return list;
    }

}
