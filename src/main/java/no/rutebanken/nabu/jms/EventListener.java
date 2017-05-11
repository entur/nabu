package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.jms.dto.EventDTO;
import no.rutebanken.nabu.jms.mapper.EventMapper;
import no.rutebanken.nabu.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventRepository eventRepository;

    private EventMapper eventMapper=new EventMapper();

    @JmsListener(destination = "NabuEvent")
    public void processMessage(String content) {
        EventDTO dto = EventDTO.fromString(content);

        logger.info("Received event: " + dto.toString());

        Event event = eventMapper.toEvent(dto);
        eventRepository.save(event);
    }




}
