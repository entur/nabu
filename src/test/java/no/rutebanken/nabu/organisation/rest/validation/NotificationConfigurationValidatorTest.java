package no.rutebanken.nabu.organisation.rest.validation;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class NotificationConfigurationValidatorTest {

    private NotificationConfigurationValidator validator = new NotificationConfigurationValidator();


    @Test(expected = IllegalArgumentException.class)
    public void validateNotificationWithoutEventFilterFails() {
        Set<NotificationConfigDTO> config = withCrudFilter();
        config.iterator().next().eventFilter = null;
        validator.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateNotificationWithoutNotificationTypeFails() {
        Set<NotificationConfigDTO> config = withCrudFilter();
        config.iterator().next().notificationType = null;
        validator.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateCrudFilterWithoutEntityClassificationsFails() {
        Set<NotificationConfigDTO> config = withCrudFilter();
        config.iterator().next().eventFilter.entityClassificationRefs.clear();
        validator.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithoutJobDomainFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.jobDomain = null;
        validator.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithNullActionFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.actions = null;
        validator.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithoutActionFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.actions = new HashSet<>();
        validator.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithNullStatenFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.states = null;
        validator.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateJobFilterWithoutStatenFails() {
        Set<NotificationConfigDTO> config = withJobFilter();
        config.iterator().next().eventFilter.states = new HashSet<>();
        validator.validate(config);
    }


    protected Set<NotificationConfigDTO> withCrudFilter() {
        NotificationConfigDTO configDTO = new NotificationConfigDTO();
        configDTO.notificationType = NotificationType.EMAIL;

        EventFilterDTO eventFilter = new EventFilterDTO(EventFilterDTO.EventFilterType.CRUD);
        eventFilter.entityClassificationRefs = Sets.newHashSet("ref1");
        configDTO.eventFilter = eventFilter;

        return Sets.newHashSet(configDTO);
    }


    protected Set<NotificationConfigDTO> withJobFilter() {
        NotificationConfigDTO configDTO = new NotificationConfigDTO();
        configDTO.notificationType = NotificationType.EMAIL;

        EventFilterDTO eventFilter = new EventFilterDTO(EventFilterDTO.EventFilterType.JOB);
        eventFilter.actions = Sets.newHashSet("BUILD");
        eventFilter.jobDomain = JobEvent.JobDomain.GEOCODER;
        eventFilter.states = Sets.newHashSet(JobState.FAILED);
        configDTO.eventFilter = eventFilter;

        return Sets.newHashSet(configDTO);
    }
}
