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

package no.rutebanken.nabu.config;

import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.NotificationProcessor;

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
