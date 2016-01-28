package no.rutebanken.nabu.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class StatusListener {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        @JmsListener(destination = "ExternalProviderStatus")
        public void processMessage(String content) {
            logger.info("Got message: " + content);
        }

}
