package no.rutebanken.nabu.event.listener;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.NabuEventValidationException;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import no.rutebanken.nabu.event.listener.mapper.EventMapper;
import no.rutebanken.nabu.exceptions.NabuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JobEventProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EventService eventService;
    private final EventMapper eventMapper = new EventMapper();

    public JobEventProcessor(EventService eventService) {
        this.eventService = eventService;
    }

    public void processMessage(String content) {
        JobEventDTO dto = JobEventDTO.fromString(content);
        Event event = eventMapper.toJobEvent(dto);
        logger.info("Received job event: {}", event);

        try {
            eventService.addEvent(event);
        } catch (NabuEventValidationException e) {
            logger.warn("Skipping job event {} with validation errors: {}", event, e.validationErrors());
        } catch (Exception e) {
            throw new NabuException("Error while saving JobEvent " + event, e);
        }
    }
}
