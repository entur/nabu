package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<JobEvent> findTimetableJobEvents(Long providerId, Instant from, Instant to, List<String> actions,
                                        List<JobState> states, List<String> externalIds, List<String> fileNames);

    List<JobEvent> getLatestTimetableFileTransfer(Long providerId);

    void clearAll(String domain);

    void clear(String domain, Long providerId);

}
