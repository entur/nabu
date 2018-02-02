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

package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByTypeAndStatus(NotificationType notificationType, Notification.NotificationStatus status);

    List<Notification> findByUserNameAndTypeAndStatus(String userName, NotificationType notificationType, Notification.NotificationStatus status);


    /**
     * Clear all notifications for events for a given job domain.
     */
    void clearAll(String domain);


    /**
     * Clear all notifications for events for a given job domain and provider combination.
     */
    void clear(String domain, Long providerId);
}
