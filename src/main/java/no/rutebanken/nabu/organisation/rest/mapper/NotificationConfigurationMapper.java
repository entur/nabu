package no.rutebanken.nabu.organisation.rest.mapper;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.organisation.model.user.NotificationConfiguration;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.EventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;
import no.rutebanken.nabu.organisation.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organisation.repository.EntityClassificationRepository;
import no.rutebanken.nabu.organisation.repository.OrganisationRepository;
import no.rutebanken.nabu.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationConfigurationMapper {

    @Autowired
    private EntityClassificationRepository entityClassificationRepository;

    @Autowired
    private AdministrativeZoneRepository administrativeZoneRepository;

    @Autowired
    private OrganisationRepository organisationRepository;


    public Set<NotificationConfigDTO> toDTO(Set<NotificationConfiguration> entity) {
        if (CollectionUtils.isEmpty(entity)) {
            return new HashSet<>();
        }

        return entity.stream().map(n -> new NotificationConfigDTO(n.getNotificationType(), n.isEnabled(),
                                                                         toDTO(n.getEventFilter()))).collect(Collectors.toSet());
    }


    public Set<NotificationConfiguration> fromDTO(Set<NotificationConfigDTO> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return new HashSet<>();
        }
        return dtos.stream().map(n -> fromDTO(n)).collect(Collectors.toSet());
    }


    private EventFilterDTO toDTO(EventFilter eventFilter) {
        EventFilterDTO dto = new EventFilterDTO();

        if (eventFilter instanceof JobEventFilter) {
            JobEventFilter jobEventFilter = (JobEventFilter) eventFilter;
            dto.type = EventFilterDTO.EventFilterType.JOB;
            dto.states = jobEventFilter.getStates();
            dto.actions = jobEventFilter.getActions();
            dto.jobDomain = JobEvent.JobDomain.valueOf(jobEventFilter.getJobDomain());
        } else if (eventFilter instanceof CrudEventFilter) {
            CrudEventFilter crudEventFilter = (CrudEventFilter) eventFilter;
            dto.type = EventFilterDTO.EventFilterType.CRUD;
            dto.entityClassificationRefs = crudEventFilter.getEntityClassifications().stream().map(ec -> ec.getId()).collect(Collectors.toSet());
            dto.administrativeZoneRefs = crudEventFilter.getAdministrativeZones().stream().map(az -> az.getId()).collect(Collectors.toSet());
        }

        if (eventFilter.getOrganisation() != null) {
            dto.organisationRef = eventFilter.getOrganisation().getId();
        }
        return dto;
    }


    // TODO recreate at every change? or match existing
    private NotificationConfiguration fromDTO(NotificationConfigDTO dto) {
        NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
        notificationConfiguration.setEnabled(dto.enabled);
        notificationConfiguration.setEventFilter(fromDTO(dto.eventFilter));
        notificationConfiguration.setNotificationType(NotificationType.valueOf(dto.notificationType.name()));
        return notificationConfiguration;
    }

    private EventFilter fromDTO(EventFilterDTO dto) {
        EventFilter eventFilter;
        if (EventFilterDTO.EventFilterType.CRUD.equals(dto.type)) {
            CrudEventFilter crudEventFilter = new CrudEventFilter();
            crudEventFilter.setEntityClassifications(dto.entityClassificationRefs.stream().map(ecr -> entityClassificationRepository.getOneByPublicId(ecr)).collect(Collectors.toSet()));
            if (!CollectionUtils.isEmpty(dto.administrativeZoneRefs)) {
                crudEventFilter.setAdministrativeZones(dto.administrativeZoneRefs.stream().map(adz -> administrativeZoneRepository.getOneByPublicId(adz)).collect(Collectors.toSet()));
            }
            eventFilter = crudEventFilter;
        } else if (EventFilterDTO.EventFilterType.JOB.equals(dto.type)) {
            JobEventFilter jobEventFilter = new JobEventFilter();
            jobEventFilter.setActions(dto.actions);
            jobEventFilter.setStates(dto.states);
            jobEventFilter.setJobDomain(dto.jobDomain.toString());
            eventFilter = jobEventFilter;
        } else {
            throw new IllegalArgumentException("Unknown event filter type: " + dto.type);
        }

        if (dto.organisationRef != null) {
            eventFilter.setOrganisation(organisationRepository.getOneByPublicId(dto.organisationRef));
        }
        return eventFilter;
    }
}
