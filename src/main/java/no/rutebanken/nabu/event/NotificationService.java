package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.repository.UserRepository;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Map<NotificationType, NotificationProcessor> notificationSenders;

    public void sendNotifications(NotificationType type) {
        NotificationProcessor notificationSender = notificationSenders.get(type);
        if (notificationSender == null) {
            throw new IllegalArgumentException("Not notification sender registered for notification type: " + type);
        }

        List<Notification> notificationList = notificationRepository.findByTypeAndStatus(type, Notification.NotificationStatus.READY);

        Map<String, Set<Notification>> notificationsPerUser = notificationList.stream().collect(Collectors.groupingBy(Notification::getUserName, Collectors.mapping(Function.identity(), Collectors.toSet())));

        notificationsPerUser.forEach((username, notifications) -> notificationSender.processNotificationsForUser(userRepository.getUserByUsername(username), notifications));
    }

}
