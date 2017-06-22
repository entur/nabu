package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Set;

@Service
public class NotificationConfigurationValidator {


    public void validate(String userName, Set<NotificationConfigDTO> config) {
        Assert.notNull(userName, "userName required");
        if (config != null) {
            for (NotificationConfigDTO notificationConfigDTO : config) {
                Assert.notNull(notificationConfigDTO.notificationType, "notifications.notificationType required");
                assertEventFilter(notificationConfigDTO.eventFilter);
            }
        }

    }


    private void assertEventFilter(EventFilterDTO eventFilter) {
        Assert.notNull(eventFilter, "notifications.eventFilter required");
        Assert.notNull(eventFilter.type, "notifications.eventFilter.type required");

        if (eventFilter.type == EventFilterDTO.EventFilterType.JOB) {
            Assert.notNull(eventFilter.jobDomain, "notifications.eventFilter.jobDomain required for JOB event filter");
            Assert.notEmpty(eventFilter.states, "notifications.eventFilter.states required for JOB event filter");
            Assert.notEmpty(eventFilter.actions, "notifications.eventFilter.actions required for JOB event filter");
        } else if (eventFilter.type == EventFilterDTO.EventFilterType.CRUD) {
            Assert.notEmpty(eventFilter.entityClassificationRefs, "notifications.eventFilter.entityClassificationRefs required for CRUD event filter");
        }
    }
}
