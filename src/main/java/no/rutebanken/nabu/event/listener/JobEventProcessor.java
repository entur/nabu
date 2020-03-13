package no.rutebanken.nabu.event.listener;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import no.rutebanken.nabu.event.listener.mapper.EventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobEventProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    private EventMapper eventMapper = new EventMapper();

    public void processMessage(String content) {
        JobEventDTO dto = JobEventDTO.fromString(content);
        Event event = eventMapper.toJobEvent(dto);
        logger.info("Received job event: {}", event);
        eventService.addEvent(event);
    }
}
