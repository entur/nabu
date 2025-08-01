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

package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.CrudEventSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<JobEvent> findTimetableJobEvents(List<Long> providerIds, Instant from, Instant to, List<String> actions,
                                        List<JobState> states, List<String> externalIds, List<String> fileNames);

    List<JobEvent> getLatestTimetableFileTransfer(Long providerId);

    /**
     * Return the unordered list of timetable job events for the given providers and correlation id.
     */
    List<JobEvent> getCorrelatedTimetableEvents(List<Long> providerIds, String correlationId);

    List<CrudEvent> findCrudEvents(CrudEventSearch search);

    void clearJobEvents(String domain);

    void clearJobEvents(String domain, Long providerId);

}
