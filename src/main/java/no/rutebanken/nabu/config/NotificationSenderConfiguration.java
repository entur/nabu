package no.rutebanken.nabu.config;

import no.rutebanken.nabu.event.NotificationSender;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class NotificationSenderConfiguration {

    @Bean
    public Map<NotificationType, NotificationSender> notificationSenders(@Autowired List<NotificationSender> notificationSenderList) {
        return notificationSenderList.stream().collect(Collectors.toMap(NotificationSender::getSupportedNotificationType,
                Function.identity()));

    }
}
