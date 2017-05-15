package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
import no.rutebanken.nabu.jms.mapper.EventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JobEventListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    private EventMapper eventMapper = new EventMapper();

    @JmsListener(destination = "JobEventQueue")
    public void processMessage(String content) {
        JobEventDTO dto = JobEventDTO.fromString(content);

        Event event = eventMapper.toJobEvent(dto);
        logger.info("Received job event: " + event);
        eventService.addEvent(event);
    }

}
