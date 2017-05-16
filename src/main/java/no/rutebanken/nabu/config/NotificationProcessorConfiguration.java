package no.rutebanken.nabu.config;

import no.rutebanken.nabu.event.NotificationProcessor;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class NotificationProcessorConfiguration {

    @Bean
    public Map<NotificationType, NotificationProcessor> notificationProcessors(@Autowired List<NotificationProcessor> notificationProcessorList) {
        return notificationProcessorList.stream().collect(Collectors.toMap(NotificationProcessor::getSupportedNotificationType,
                Function.identity()));

    }
}
