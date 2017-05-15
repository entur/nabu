package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private List<EventHandler> eventHandlers;


    public List<JobEvent> findTimetableJobEvents(Long providerId, Instant from, Instant to, List<String> actions,
                                                        List<JobState> states, List<String> externalIds, List<String> fileNames) {
        return eventRepository.findTimetableJobEvents(providerId, from, to, actions, states, externalIds, fileNames);
    }


    public void addEvent(Event event) {
        eventRepository.save(event);

        eventHandlers.forEach(handler -> handler.onEvent(event));
    }


    public void clearAll(String domain) {
        notificationRepository.clearAll(domain);
        eventRepository.clearAll(domain);
    }


    public void clear(String domain, Long providerId) {
        notificationRepository.clear(domain, providerId);
        eventRepository.clear(domain, providerId);
    }
}
