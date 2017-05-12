package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.SystemStatus;
import no.rutebanken.nabu.jms.mapper.EventMapper;
import no.rutebanken.nabu.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

// Remove when marduk is updated to send on Event format
@Component
public class SystemStatusListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EventService eventService;

    private EventMapper eventMapper = new EventMapper();

    @JmsListener(destination = "MardukSystemStatusQueue")
    public void processMessage(String content) {
        SystemStatus systemStatus = SystemStatus.fromString(content);
        logger.info("Received system status event: " + systemStatus.toString());

        eventService.addEvent(eventMapper.toJobEvent(systemStatus));
    }
}
