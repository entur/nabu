package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.status.Status;
import no.rutebanken.nabu.status.StatusRepository;
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
    public static final String CORRELATION_ID = "RutebankenCorrelationId";
    public static final String CAMEL_FILE_NAME = "CamelFileName";

    private final JmsTemplate jmsTemplate;

    @Autowired
    public JmsSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Autowired
    StatusRepository statusRepository;

    public void sendBlobMessage(String destinationName, File file, String fileName, Long providerId) {
        String correlationId = "" + System.currentTimeMillis();
        this.jmsTemplate.send(destinationName, session -> createBlobMessage(session, file, fileName, providerId, correlationId));
        statusRepository.update(new Status(fileName, providerId, Status.Action.FILE_TRANSFER, Status.State.STARTED, correlationId));
    }

    private Message createBlobMessage(Session session, File file, String fileName, Long providerId, String correlationId) {
        try {
            BlobMessage message = ((ActiveMQSession) session).createBlobMessage(file);
            message.setLongProperty(PROVIDER_ID, providerId);
            message.setStringProperty(CAMEL_FILE_NAME, fileName);
            message.setStringProperty(CORRELATION_ID, correlationId);
            message.setName(fileName);
            return message;
        } catch (JMSException e) {
            statusRepository.update(new Status(fileName, providerId, Status.Action.FILE_TRANSFER, Status.State.FAILED, correlationId));
            throw new RuntimeException(e);
        }
    }

}