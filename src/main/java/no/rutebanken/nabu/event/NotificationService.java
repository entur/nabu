package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.repository.UserRepository;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Map<NotificationType, NotificationProcessor> notificationSenders;

    public void sendNotifications(NotificationType type) {
        logger.info("About to send notifications of type: " + type);
        NotificationProcessor notificationSender = notificationSenders.get(type);
        if (notificationSender == null) {
            throw new IllegalArgumentException("No notification sender registered for notification type: " + type);
        }

        List<Notification> notificationList = notificationRepository.findByTypeAndStatus(type, Notification.NotificationStatus.READY);

        Map<String, Set<Notification>> notificationsPerUser = notificationList.stream().collect(Collectors.groupingBy(Notification::getUserName, Collectors.mapping(Function.identity(), Collectors.toSet())));
        notificationsPerUser.forEach((username, notifications) -> sendNotificationsForUser(notificationSender, username, notifications));

        logger.info("Finished sending " + notificationList.size() + " notifications of type: " + type);
    }


    private void sendNotificationsForUser(NotificationProcessor notificationSender, String userName, Set<Notification> notifications) {
        User user = userRepository.getUserByUsername(userName);
        if (user != null) {
            notificationSender.processNotificationsForUser(user, notifications);
        } else {
            logger.warn("Cannot send notifications to unknown user: " + userName + ". Discarding notifications: " + notifications);
            notificationRepository.delete(notifications);
        }

    }
}
