package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.repository.StatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class StatusListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatusRepository statusRepository;

    @JmsListener(destination = "ExternalProviderStatus")
    public void processMessage(String content) {
        Status status = Status.fromString(content);
        logger.info("Received job status update: " + status.toString());
        statusRepository.add(status);
    }

}
