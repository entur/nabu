/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.user.UserRepository;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
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
public class ScheduledNotificationService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Map<NotificationType, NotificationProcessor> notificationSenders;

    public void sendNotifications(NotificationType type) {
        logger.info("About to send notifications of type: {}", type);
        NotificationProcessor notificationSender = notificationSenders.get(type);
        if (notificationSender == null) {
            throw new IllegalArgumentException("No notification sender registered for notification type: " + type);
        }

        List<Notification> notificationList = notificationRepository.findByTypeAndStatus(type, Notification.NotificationStatus.READY);

        Map<String, Set<Notification>> notificationsPerUser = notificationList.stream().collect(Collectors.groupingBy(Notification::getUserName, Collectors.mapping(Function.identity(), Collectors.toSet())));
        notificationsPerUser.forEach((username, notifications) -> sendNotificationsForUser(notificationSender, username, notifications));

        logger.info("Finished sending {} notifications of type: {}", notificationList.size(), type);
    }

    private void sendNotificationsForUser(NotificationProcessor notificationSender, String userName, Set<Notification> notifications) {
        UserDTO user = userRepository.getByUsername(userName);
        if (user != null) {
            notificationSender.processNotificationsForUser(user, notifications);
        } else {
            logger.warn("Cannot send notifications to unknown user: {}. Discarding notifications: {}", userName, notifications);
            notificationRepository.deleteAll(notifications);
        }

    }
}
