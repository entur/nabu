package no.rutebanken.nabu.config;

import no.rutebanken.nabu.event.NotificationProcessor;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class NotificationProcessorConfiguration {

    @Bean
    public Map<NotificationType, NotificationProcessor> notificationProcessors(@Autowired List<NotificationProcessor> notificationProcessorList) {
        Map<NotificationType, NotificationProcessor> processorMap = new HashMap<>();
        notificationProcessorList.forEach(np -> np.getSupportedNotificationTypes().forEach(nt -> processorMap.put(nt, np)));
        return processorMap;
    }
}
