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

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ImmediateNotificationService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Map<NotificationType, NotificationProcessor> notificationSenders;

    public void sendNotifications(Notification notification, UserDTO user) {
        NotificationType type = notification.getType();
        logger.info("About to send notifications of type: " + type + " to user " + user.getUsername());
        NotificationProcessor notificationSender = notificationSenders.get(type);
        if (notificationSender == null) {
            throw new IllegalArgumentException("No notification sender registered for notification type: " + type);
        }

        notificationSender.processNotificationsForUser(user, Sets.newHashSet(notification));
    }

}
