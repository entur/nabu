package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.status.Status;
import no.rutebanken.nabu.status.StatusRepository;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.jms.Topic;

@Component
public class StatusListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatusRepository statusRepository;

    @JmsListener(destination = "ExternalProviderStatus")
    public void processMessage(String content) {
        logger.debug("Got message: " + content);
        Status status = Status.fromString(content);
        logger.debug("Converted to: " + status.toString());
        logger.info("Received message. Updating status repository with status: " + status.toString());
        statusRepository.update(status);
    }

}
