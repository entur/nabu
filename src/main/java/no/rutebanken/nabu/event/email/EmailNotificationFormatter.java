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

package no.rutebanken.nabu.event.email;

import freemarker.template.Configuration;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.exceptions.NabuException;
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Format email notifications for events.
 */
@Service
public class EmailNotificationFormatter {

    @Autowired
    private ProviderRepository providerRepository;


    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Configuration freemarkerConfiguration;

    @Value("${email.link.operator:https://operator.rutebanken.org/}")
    private String operatorLink;
    @Value("${email.link.stop.place:https://stoppested.entur.org/}")
    private String stopPlaceLink;
    @Value("${email.link.routedb:https://rutedb.entur.org/}")
    private String routedbLink;

    @Value("${notification.email.max.length:200}")
    private int emailNotificationMaxEvents;


    public String getSubject(Locale locale) {
        return messageSource.getMessage("notification.email.subject", new Object[]{}, locale);
    }

    public String formatMessage(Set<Notification> notifications, Locale locale) {
        return formatMessage(notifications, locale, providerRepository.getProviders());
    }

    protected String formatMessage(Set<Notification> notifications, Locale locale, Collection<Provider> providers) {
        Collection<Notification> filteredNotifications;
        if (notifications.size() > emailNotificationMaxEvents) {
            List<Notification> chronologicalNotifications = new ArrayList<>(notifications);
            chronologicalNotifications.sort(Comparator.comparing(o -> o.getEvent().getEventTime()));

            filteredNotifications = chronologicalNotifications.subList(notifications.size() - emailNotificationMaxEvents, notifications.size());
        } else {
            filteredNotifications = notifications;
        }


        Map<String, SortedSet<JobEvent>> jobEventsPerDomain = filteredNotifications.stream().filter(n -> n.getEvent() instanceof JobEvent).map(n -> (JobEvent) n.getEvent())
                                                                      .collect(Collectors.groupingBy(JobEvent::getDomain, Collectors.mapping(Function.identity(), Collectors.toCollection(TreeSet::new))));


        Map<String, SortedSet<CrudEvent>> crudEventsPerEntityType = filteredNotifications.stream().filter(n -> n.getEvent() instanceof CrudEvent).map(n -> (CrudEvent) n.getEvent())
                                                                            .collect(Collectors.groupingBy(CrudEvent::getEntityType, Collectors.mapping(Function.identity(), Collectors.toCollection(TreeSet::new))));


        Map<String, Object> model = new HashMap<>();

        model.put("jobEvents", jobEventsPerDomain);
        model.put("crudEvents", crudEventsPerEntityType);
        model.put("message", new MessageResolverMethod(messageSource,locale));
        model.put("formatTime",new TemporalAccessorFormatter());
        model.put("notificationConfigurationLink", operatorLink);
        model.put("stopPlaceLinkPrefix", stopPlaceLink+"stop_place/");
        model.put("jobLink", new JobLinkResolverMethod(routedbLink + "referentials/"));
        model.put("emailNotificationMaxEvents", emailNotificationMaxEvents);
        model.put("totalNotificationsCnt", notifications.size());

        Map<String, String> providerMap = providers.stream().collect(Collectors.toMap(provider -> provider.getId().toString(), Provider::getName));
        model.put("providers", providerMap);

        return getFreeMarkerTemplateContent(model);
    }

    public String getFreeMarkerTemplateContent(Map<String, Object> model) {
        try {

            return FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfiguration.getTemplate("fm_email_notification_template.ftl"), model);
        } catch (Exception e) {
            throw new NabuException("Exception occurred while processing email template", e);
        }

    }




}
