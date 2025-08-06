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

import jakarta.ws.rs.NotFoundException;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.rest.openapi.api.StatusApi;
import no.rutebanken.nabu.rest.openapi.model.DataDeliveryStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static no.rutebanken.nabu.domain.event.JobState.ERROR_JOB_STATES;
import static no.rutebanken.nabu.event.support.DateUtils.atDefaultOffset;

@Component
public class TimetableDataDeliveryStatusResource implements StatusApi {

    private final EventRepository eventRepository;
    private final ProviderRepository providerRepository;

    public TimetableDataDeliveryStatusResource(EventRepository eventRepository, ProviderRepository providerRepository) {
        this.eventRepository = eventRepository;
        this.providerRepository = providerRepository;
    }

    @Override
    @PreAuthorize("@authorizationService.canViewTimetableDataEvent(#codespace)")
    public DataDeliveryStatus getDataDeliveryStatus(String codespace, String correlationId) {
        Provider provider = providerRepository.getProvider(codespace);
        if (provider == null) {
            throw new IllegalStateException("Provider not found");
        }
        List<JobEvent> events = eventRepository.getCorrelatedTimetableEvents(List.of(provider.getId(), provider.getChouetteInfo().migrateDataToProvider), correlationId);
        if (events.isEmpty()) {
            throw new NotFoundException("Correlation id not found");
        }
        JobEvent firstEvent = events.stream().min(Comparator.comparing(Event::getEventTime)).orElseThrow();

        return new DataDeliveryStatus()
                .date(atDefaultOffset(firstEvent.getEventTime()))
                .fileName(firstEvent.getName())
                .state(state(events));

    }


    /**
     * Return the state of the delivery given a set of statuses.
     * Some steps in the import pipeline can run in parallel, thus the chronological order is arbitrary.
     * In particular, the OTP build graph event is not necessarily the last event recorded in the pipeline.
     */

    private static DataDeliveryStatus.StateEnum state(List<JobEvent> statuses) {
        if (statuses.stream().anyMatch(e -> TimeTableAction.OTP2_BUILD_GRAPH.toString().equals(e.getAction()) && JobState.OK.equals(e.getState()))) {
            return DataDeliveryStatus.StateEnum.OK;
        } else if (statuses.stream().anyMatch(e -> ERROR_JOB_STATES.contains(e.getState()))) {
            return DataDeliveryStatus.StateEnum.FAILED;
        } else {
            return DataDeliveryStatus.StateEnum.IN_PROGRESS;
        }
    }


}
