package no.rutebanken.nabu.organisation.rest.mapper;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.model.user.ContactDetails;
import no.rutebanken.nabu.organisation.model.user.NotificationConfiguration;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.EventFilter;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;
import no.rutebanken.nabu.organisation.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organisation.repository.EntityClassificationRepository;
import no.rutebanken.nabu.organisation.repository.OrganisationRepository;
import no.rutebanken.nabu.organisation.repository.ResponsibilitySetRepository;
import no.rutebanken.nabu.organisation.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserMapper implements DTOMapper<User, UserDTO> {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ResponsibilitySetRepository responsibilitySetRepository;


    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private ResponsibilitySetMapper responsibilitySetMapper;

    @Autowired
    private EntityClassificationRepository entityClassificationRepository;

    @Autowired
    private AdministrativeZoneRepository administrativeZoneRepository;

    public UserDTO toDTO(User org, boolean fullDetails) {
        UserDTO dto = new UserDTO();
        dto.id = org.getId();
        dto.username = org.getUsername();

        dto.contactDetails = toDTO(org.getContactDetails());
        dto.notifications = toDTO(org.getNotificationConfigurations());
        dto.responsibilitySetRefs = toRefList(org.getResponsibilitySets());
        dto.organisationRef = org.getOrganisation().getId();

        if (fullDetails) {
            dto.organisation = organisationMapper.toDTO(org.getOrganisation(), false);
            dto.responsibilitySets = org.getResponsibilitySets().stream().map(rs -> responsibilitySetMapper.toDTO(rs, false)).collect(Collectors.toList());
        }

        return dto;
    }

    public User createFromDTO(UserDTO dto, Class<User> clazz) {
        User entity = new User();
        entity.setPrivateCode(UUID.randomUUID().toString());
        return updateFromDTO(dto, entity);
    }

    public User updateFromDTO(UserDTO dto, User entity) {
        entity.setUsername(dto.username.toLowerCase());

        entity.setContactDetails(fromDTO(dto.contactDetails));

        if (dto.organisationRef != null) {
            entity.setOrganisation(organisationRepository.getOneByPublicId(dto.organisationRef));
        }
        if (CollectionUtils.isEmpty(dto.responsibilitySetRefs)) {
            entity.setResponsibilitySets(new HashSet<>());
        } else {
            entity.setResponsibilitySets(dto.responsibilitySetRefs.stream().map(ref -> responsibilitySetRepository.getOneByPublicId(ref)).collect(Collectors.toSet()));
        }


        if (CollectionUtils.isEmpty(dto.notifications)) {
            entity.setNotificationConfigurations(new HashSet<>());
        } else {
            entity.setNotificationConfigurations(dto.notifications.stream().map(n -> fromDTO(n)).collect(Collectors.toSet()));
        }

        return entity;
    }

    // TODO recreate at every change? or match existing
    private NotificationConfiguration fromDTO(NotificationConfigDTO dto) {
        NotificationConfiguration notificationConfiguration = new NotificationConfiguration();

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
            jobEventFilter.setAction(dto.action);
            jobEventFilter.setState(dto.state);
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


    private ContactDetailsDTO toDTO(ContactDetails entity) {
        if (entity == null) {
            return null;
        }
        ContactDetailsDTO dto = new ContactDetailsDTO();
        dto.email = entity.getEmail();
        dto.firstName = entity.getFirstName();
        dto.lastName = entity.getLastName();
        dto.phone = entity.getPhone();
        return dto;
    }

    private ContactDetails fromDTO(ContactDetailsDTO dto) {
        if (dto == null) {
            return null;
        }
        ContactDetails entity = new ContactDetails();
        entity.setFirstName(dto.firstName);
        entity.setLastName(dto.lastName);
        entity.setEmail(dto.email);
        entity.setPhone(dto.phone);
        return entity;
    }

    private List<String> toRefList(Set<ResponsibilitySet> responsibilitySetSet) {
        if (responsibilitySetSet == null) {
            return null;
        }
        return responsibilitySetSet.stream().map(rs -> rs.getId()).collect(Collectors.toList());
    }

    private List<NotificationConfigDTO> toDTO(Set<NotificationConfiguration> entity) {
        if (CollectionUtils.isEmpty(entity)) {
            return null;
        }

        return entity.stream().map(n -> new NotificationConfigDTO(n.getNotificationType(),
                                                                         toDTO(n.getEventFilter()))).collect(Collectors.toList());
    }

    private EventFilterDTO toDTO(EventFilter eventFilter) {
        EventFilterDTO dto = new EventFilterDTO();

        if (eventFilter instanceof JobEventFilter) {
            JobEventFilter jobEventFilter = (JobEventFilter) eventFilter;
            dto.type = EventFilterDTO.EventFilterType.JOB;
            dto.state = jobEventFilter.getState();
            dto.action = jobEventFilter.getAction();
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

}
