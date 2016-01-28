package no.rutebanken.nabu.jms;

import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.File;

@Component
public class JmsSender {

    public static final String PROVIDER_ID = "RutebankenProviderId";
    public static final String CAMEL_FILE_NAME = "CamelFileName";

    private final JmsTemplate jmsTemplate;

    @Autowired
    public JmsSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendBlobMessage(String destinationName, File file, String fileName, Long providerId) {
        this.jmsTemplate.send(destinationName, session -> createBlobMessage(session, file, fileName, providerId));
    }

    private Message createBlobMessage(Session session, File file, String fileName, Long providerId) {
        try {
            BlobMessage message = ((ActiveMQSession) session).createBlobMessage(file);
            message.setLongProperty(PROVIDER_ID, providerId);
            message.setStringProperty(CAMEL_FILE_NAME, fileName);
            message.setName(fileName);
            return message;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

}