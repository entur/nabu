package no.rutebanken.nabu.event.email;

import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    @Value("${notification.operator.link:https://operator.rutebanken.org/}")
    private String notificationConfigurationLink;
    @Value("${notification.stop.place.link.prefix:https://stoppested.entur.org/edit/}")
    private String stopPlaceLinkPrefix;
    @Value("${notification.timetable.link.prefix:https://rutedb.entur.org/referentials/}")
    private String timetableJobLinkPrefix;
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
            Collections.sort(chronologicalNotifications, (o1, o2) -> o1.getEvent().getEventTime().compareTo(o2.getEvent().getEventTime()));

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
        model.put("message", new MessageResolverMethod(locale));
        model.put("notificationConfigurationLink", notificationConfigurationLink);
        model.put("stopPlaceLinkPrefix", stopPlaceLinkPrefix);
        model.put("jobLink", new JobLinkResolverMethod(timetableJobLinkPrefix));
        model.put("emailNotificationMaxEvents", emailNotificationMaxEvents);
        model.put("totalNotificationsCnt", notifications.size());

        Map<String, String> providerMap = providers.stream().collect(Collectors.toMap(provider -> provider.getId().toString(), Provider::getName));
        model.put("providers", providerMap);

        return geFreeMarkerTemplateContent(model);
    }

    public String geFreeMarkerTemplateContent(Map<String, Object> model) {
        try {

            return FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfiguration.getTemplate("fm_email_notification_template.ftl"), model);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while processing email template:" + e.getMessage(), e);
        }

    }


    private class MessageResolverMethod implements TemplateMethodModelEx {

        private Locale locale;

        public MessageResolverMethod(Locale locale) {
            this.locale = locale;
        }


        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() < 1) {
                throw new TemplateModelException("Wrong number of arguments");
            }
            SimpleScalar arg = (SimpleScalar) arguments.get(0);
            String code = arg.getAsString();
            if (code == null || code.isEmpty()) {
                throw new TemplateModelException("Invalid code value '" + code + "'");
            }

            Object[] argsArray = null;
            if (arguments.size() > 1) {
                argsArray = arguments.subList(1, arguments.size()).toArray();
            }

            return messageSource.getMessage(code, argsArray, code, locale);
        }
    }

}
