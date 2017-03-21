package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.organization.Organisation;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organization.repository.EntityClassificationRepository;
import no.rutebanken.nabu.organization.repository.OrganisationRepository;
import no.rutebanken.nabu.organization.repository.RoleRepository;
import no.rutebanken.nabu.organization.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.nabu.organization.rest.dto.responsibility.ResponsibilitySetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResponsibilitySetMapper extends BaseDTOMapper<ResponsibilitySet, ResponsibilitySetDTO> {
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Autowired
	private AdministrativeZoneRepository administrativeZoneRepository;

	@Autowired
	private EntityClassificationRepository entityClassificationRepository;

	public ResponsibilitySet createFromDTO(ResponsibilitySetDTO dto, Class<ResponsibilitySet> clazz) {
		ResponsibilitySet entity = fromDTOBasics(new ResponsibilitySet(), dto);

		entity.setName(dto.name);

		if (!CollectionUtils.isEmpty(dto.roles)) {
			dto.roles.forEach(d -> d.codeSpace = dto.codeSpace);
			entity.setRoles(dto.roles.stream().map(ra -> fromDTO(ra, entity.getCodeSpace())).collect(Collectors.toSet()));
		}

		return entity;
	}

	@Override
	public ResponsibilitySet updateFromDTO(ResponsibilitySetDTO dto, ResponsibilitySet entity) {
		return null; // TODO
	}

	public ResponsibilitySetDTO toDTO(ResponsibilitySet org) {
		ResponsibilitySetDTO dto = toDTOBasics(org, new ResponsibilitySetDTO());

		dto.name = org.getName();

		dto.roles = org.getRoles().stream().map(ra -> toDTO(ra)).collect(Collectors.toList());
		return dto;
	}

	private ResponsibilityRoleAssignmentDTO toDTO(ResponsibilityRoleAssignment entity) {
		ResponsibilityRoleAssignmentDTO dto = new ResponsibilityRoleAssignmentDTO();
		dto.id = entity.getId();
		dto.responsibleOrganisationRef = entity.getResponsibleOrganisation().getId();
		dto.typeOfResponsibilityRoleRef = entity.getTypeOfResponsibilityRole().getId();

		if (entity.getResponsibleArea() != null) {
			dto.typeOfResponsibilityRoleRef = entity.getResponsibleArea().getId();
		}
		if (!CollectionUtils.isEmpty(entity.getResponsibleEntityClassifications())) {
			dto.entityClassificationRefs = entity.getResponsibleEntityClassifications().stream().map(ec -> ec.getId()).collect(Collectors.toList());
		}

		return dto;
	}

	private ResponsibilityRoleAssignment fromDTO(ResponsibilityRoleAssignmentDTO dto, CodeSpace codeSpace) {
		ResponsibilityRoleAssignment entity = new ResponsibilityRoleAssignment();
		entity.setCodeSpace(codeSpace);
		entity.setPrivateCode(UUID.randomUUID().toString());

		entity.setTypeOfResponsibilityRole(roleRepository.getOneByPublicId(dto.typeOfResponsibilityRoleRef));
		entity.setResponsibleOrganisation(organisationRepository.getOneByPublicId(dto.responsibleOrganisationRef));
		if (dto.responsibleAreaRef != null) {
			entity.setResponsibleArea(administrativeZoneRepository.getOneByPublicId(dto.responsibleAreaRef));
		}

		if (!CollectionUtils.isEmpty(dto.entityClassificationRefs)) {
			entity.setResponsibleEntityClassifications(dto.entityClassificationRefs.stream().map(ecr -> entityClassificationRepository.getOneByPublicId(ecr)).collect(Collectors.toSet()));
		}
		return entity;
	}
}
