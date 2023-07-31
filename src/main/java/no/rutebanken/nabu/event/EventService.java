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

package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    private final NotificationRepository notificationRepository;

    private final List<EventHandler> eventHandlers;
    private final Validator validator;

    public EventService(EventRepository eventRepository, NotificationRepository notificationRepository, List<EventHandler> eventHandlers) {
        this.eventRepository = eventRepository;
        this.notificationRepository = notificationRepository;
        this.eventHandlers = eventHandlers;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }


    public List<JobEvent> findTimetableJobEvents(List<Long> providerIds, Instant from, Instant to, List<String> actions,
                                                 List<JobState> states, List<String> externalIds, List<String> fileNames) {
        return eventRepository.findTimetableJobEvents(providerIds, from, to, actions, states, externalIds, fileNames);
    }


    public void addEvent(Event event) throws NabuEventValidationException {
        Set<ConstraintViolation<Event>> validationErrors = validator.validate(event);
        if (validationErrors.isEmpty()) {
            eventRepository.save(event);
            eventHandlers.forEach(handler -> handler.onEvent(event));
        } else {
            throw new NabuEventValidationException("Error while validating event", validationErrors);
        }
    }


    public void clearAll(String domain) {
        notificationRepository.clearAll(domain);
        eventRepository.clearJobEvents(domain);
    }


    public void clear(String domain, Long providerId) {
        notificationRepository.clear(domain, providerId);
        eventRepository.clearJobEvents(domain, providerId);
    }
}
