package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.jms.dto.CrudEventDTO;
import no.rutebanken.nabu.jms.mapper.EventMapper;
import no.rutebanken.nabu.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class CrudEventListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    private EventMapper eventMapper = new EventMapper();

    @JmsListener(destination = "CrudEventQueue")
    public void processMessage(String content) {
        CrudEventDTO dto = CrudEventDTO.fromString(content);

        Event event = eventMapper.toCrudEvent(dto);
        logger.info("Received crud event: " + event);
        eventService.addEvent(event);
    }


}
