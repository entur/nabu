package no.rutebanken.nabu.event.listener;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.listener.dto.CrudEventDTO;
import no.rutebanken.nabu.event.listener.mapper.EventMapper;
import no.rutebanken.nabu.exceptions.NabuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CrudeEventProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventService eventService;

    private final EventMapper eventMapper = new EventMapper();

    public CrudeEventProcessor(EventService eventService) {
        this.eventService = eventService;
    }

    public void processMessage(String content) {
        CrudEventDTO dto = CrudEventDTO.fromString(content);
        Event event = eventMapper.toCrudEvent(dto);
        logger.info("Received crud event: {}", event);

        try {
            eventService.addEvent(event);
        } catch(Exception e) {
            throw new NabuException("Error while saving CrudeEvent " + event, e);
        }
    }
}
