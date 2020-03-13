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

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.event.filter.EventMatcherFactory;
import no.rutebanken.nabu.event.user.UserRepository;
import no.rutebanken.nabu.event.user.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Create notifications for user with configured events filter matching a events.
 */
@Service
public class UserNotificationEventHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ImmediateNotificationService immediateNotificationService;

    @Autowired
    private EventMatcherFactory eventMatcherFactory;

    @Override
    public void onEvent(Event event) {
        userRepository.findAll().forEach(user -> createNotificationsForUser(user, event));
    }

    void createNotificationsForUser(UserDTO user, Event event) {

        if (CollectionUtils.isEmpty(user.getNotifications())) {
            return;
        }

        user.getNotifications().stream()
                .filter(notificationConfiguration -> notificationConfiguration.isEnabled())
                .filter(notificationConfig -> eventMatcherFactory.createEventMatcher(notificationConfig.getEventFilter()).matches(event))
                .forEach(notificationConfig -> createNotification(user, notificationConfig, event));

    }

    private void createNotification(UserDTO user, NotificationConfigDTO notificationConfig, Event event) {
        Notification notification = new Notification(user.getUsername(), notificationConfig.getNotificationType(), event);

        if (notification.getType().isImmediate()) {
            immediateNotificationService.sendNotifications(notification, user);
        } else {
            logger.debug("Registered new notification: {}", notification);
            notificationRepository.save(notification);
        }

    }


    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
