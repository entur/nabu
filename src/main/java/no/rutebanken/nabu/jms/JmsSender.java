package no.rutebanken.nabu.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class JmsSender {

    public static final String PROVIDER_ID = "RutebankenProviderId";
    public static final String CORRELATION_ID = "RutebankenCorrelationId";
    public static final String FILE_HANDLE = "RutebankenFileHandle";
    public static final String FILE_NAME = "RutebankenFileName";

    private final JmsTemplate jmsTemplate;

    @Autowired
    public JmsSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendBlobNotificationMessage(String destinationName, String blobName, String fileName, Long providerId, String correlationId) {
        this.jmsTemplate.send(destinationName, session -> createMessage(session, blobName, fileName, providerId, correlationId));
    }

    private Message createMessage(Session session, String blobName, String fileName, Long providerId, String correlationId) {
        try {
            Message message = session.createMessage();
            message.setLongProperty(PROVIDER_ID, providerId);
            message.setStringProperty(FILE_HANDLE, blobName);
            message.setStringProperty(CORRELATION_ID, correlationId);
            message.setStringProperty(FILE_NAME, fileName);
            return message;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

}