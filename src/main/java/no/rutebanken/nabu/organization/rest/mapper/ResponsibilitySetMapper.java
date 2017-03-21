package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.organization.Organisation;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.repository.*;
import no.rutebanken.nabu.organization.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.nabu.organization.rest.dto.responsibility.ResponsibilitySetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResponsibilitySetMapper implements DTOMapper<ResponsibilitySet, ResponsibilitySetDTO> {
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Autowired
	private AdministrativeZoneRepository administrativeZoneRepository;

	@Autowired
	private EntityClassificationRepository entityClassificationRepository;

	@Autowired
	protected CodeSpaceRepository codeSpaceRepository;

	public ResponsibilitySet createFromDTO(ResponsibilitySetDTO dto, Class<ResponsibilitySet> clazz) {
		ResponsibilitySet entity = new ResponsibilitySet();
		entity.setPrivateCode(dto.privateCode);
		entity.setCodeSpace(codeSpaceRepository.getOneByPublicId(dto.codeSpace));
		entity.setName(dto.name);

		if (!CollectionUtils.isEmpty(dto.roles)) {
			entity.setRoles(dto.roles.stream().map(ra -> fromDTO(ra, entity.getCodeSpace())).collect(Collectors.toSet()));
		}

		return entity;
	}

	@Override
	public ResponsibilitySet updateFromDTO(ResponsibilitySetDTO dto, ResponsibilitySet entity) {
		entity.setName(dto.name);
		Set<ResponsibilityRoleAssignment> roleAssignmentSet = new HashSet<>();

		for (ResponsibilityRoleAssignmentDTO dtoRole : dto.roles) {
			ResponsibilityRoleAssignment assignment = entity.getResponsibilityRoleAssignment(dtoRole.id);
			if (assignment == null) {
				assignment = fromDTO(dtoRole, entity.getCodeSpace());
			} else {
				assignment = fromDTO(dtoRole, assignment);
			}
			roleAssignmentSet.add(assignment);
		}

		entity.setRoles(roleAssignmentSet);
		return entity;
	}

	public ResponsibilitySetDTO toDTO(ResponsibilitySet entity) {
		ResponsibilitySetDTO dto = new ResponsibilitySetDTO();
		dto.id = entity.getId();
		dto.name = entity.getName();
		dto.roles = entity.getRoles().stream().map(ra -> toDTO(ra)).collect(Collectors.toList());
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

		return fromDTO(dto, entity);
	}

	private ResponsibilityRoleAssignment fromDTO(ResponsibilityRoleAssignmentDTO dto, ResponsibilityRoleAssignment entity) {
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
