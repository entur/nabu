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
import no.rutebanken.nabu.event.user.dto.user.UserDTO;

import java.util.Set;

public interface NotificationProcessor {

    /**
     * Handle a set not notifications for a user.
     *
     * Processor is expected to delete/mark as read notifications in a manner transactionally suitable to the processing.
     */
    void processNotificationsForUser(UserDTO user, Set<Notification> notifications);

    Set<NotificationType> getSupportedNotificationTypes();

}
