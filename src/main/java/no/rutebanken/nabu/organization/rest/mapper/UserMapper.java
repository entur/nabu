package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.model.user.ContactDetails;
import no.rutebanken.nabu.organization.model.user.Notification;
import no.rutebanken.nabu.organization.model.user.User;
import no.rutebanken.nabu.organization.repository.OrganisationRepository;
import no.rutebanken.nabu.organization.repository.ResponsibilitySetRepository;
import no.rutebanken.nabu.organization.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.nabu.organization.rest.dto.user.NotificationDTO;
import no.rutebanken.nabu.organization.rest.dto.user.UserDTO;
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

	public UserDTO toDTO(User org) {
		UserDTO dto = new UserDTO();
		dto.id = org.getId();
		dto.username = org.getUsername();

		dto.contactDetails = toDTO(org.getContactDetails());
		dto.notifications = toDTO(org.getNotifications());
		dto.responsibilitySetRefs = toRefList(org.getResponsibilitySets());
		dto.organisationRef = org.getOrganisation().getId();
		return dto;
	}

	public User createFromDTO(UserDTO dto, Class<User> clazz) {
		User entity = new User();
		entity.setPrivateCode(UUID.randomUUID().toString());
		return updateFromDTO(dto, entity);
	}

	public User updateFromDTO(UserDTO dto, User entity) {
		entity.setUsername(dto.username);

		entity.setContactDetails(fromDTO(dto.contactDetails));

		if (dto.organisationRef != null) {
			entity.setOrganisation(organisationRepository.getOneByPublicId(dto.organisationRef));
		}
		if (!CollectionUtils.isEmpty(dto.responsibilitySetRefs)) {
			entity.setResponsibilitySets(dto.responsibilitySetRefs.stream().map(ref -> responsibilitySetRepository.getOneByPublicId(ref)).collect(Collectors.toSet()));
		} else {
			entity.setResponsibilitySets(new HashSet<>());
		}
		return entity;
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

	private List<NotificationDTO> toDTO(Set<Notification> entity) {
		if (CollectionUtils.isEmpty(entity)) {
			return null;
		}

		return entity.stream().map(n -> new NotificationDTO(
				                                                   NotificationDTO.NotificationType.valueOf(n.getNotificationType().name()),
				                                                   n.getTrigger())).collect(Collectors.toList());
	}


}
