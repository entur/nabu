package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.organization.Authority;
import no.rutebanken.nabu.organization.model.organization.Organisation;
import no.rutebanken.nabu.organization.model.organization.OrganisationPart;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organization.repository.CodeSpaceRepository;
import no.rutebanken.nabu.organization.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organization.rest.dto.organisation.OrganisationPartDTO;
import no.rutebanken.nabu.organization.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.nabu.organization.rest.dto.responsibility.ResponsibilitySetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.BadRequestException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganisationMapper implements DTOMapper<Organisation, OrganisationDTO> {

	@Autowired
	protected CodeSpaceRepository codeSpaceRepository;

	@Autowired
	private AdministrativeZoneRepository administrativeZoneRepository;

	public OrganisationDTO toDTO(Organisation entity, boolean fullDetails) {
		OrganisationDTO dto = new OrganisationDTO();
		dto.id = entity.getId();
		dto.privateCode = entity.getPrivateCode();
		dto.codeSpace = entity.getCodeSpace().getId();
		dto.companyNumber = entity.getCompanyNumber();
		dto.name = entity.getName();

		if (entity instanceof Authority) {
			dto.organisationType = OrganisationDTO.OrganisationType.AUTHORITY;
		}

		if (!CollectionUtils.isEmpty(entity.getParts())) {
			dto.parts = entity.getParts().stream().map(p -> toDTO(p)).collect(Collectors.toList());
		}

		return dto;
	}

	@Override
	public Organisation createFromDTO(OrganisationDTO dto, Class<Organisation> clazz) {
		Organisation entity = createByType(dto.organisationType);
		entity.setCodeSpace(codeSpaceRepository.getOneByPublicId(dto.codeSpace));
		entity.setPrivateCode(dto.privateCode);
		return updateFromDTO(dto, entity);
	}

	@Override
	public Organisation updateFromDTO(OrganisationDTO dto, Organisation entity) {

		entity.setName(dto.name);
		entity.setCompanyNumber(dto.companyNumber);


		if (dto.parts == null) {
			entity.getParts().clear();
		} else {
			mergeParts(dto, entity);
		}


		return entity;
	}

	protected void mergeParts(OrganisationDTO dto, Organisation entity) {
		Set<OrganisationPart> removed = new HashSet<>(entity.getParts());
		for (OrganisationPartDTO dtoPart : dto.parts) {
			if (dtoPart.id != null) {
				OrganisationPart part = entity.getOrganisationPart(dtoPart.id);
				removed.remove(part);
				fromDTO(dtoPart, part);
			} else {
				entity.getParts().add(fromDTO(dtoPart, entity.getCodeSpace()));
			}
		}
		entity.getParts().removeAll(removed);
	}

	private OrganisationPartDTO toDTO(OrganisationPart part) {
		OrganisationPartDTO dto = new OrganisationPartDTO();
		dto.name = part.getName();
		dto.id = part.getId();
		if (!CollectionUtils.isEmpty(part.getAdministrativeZones())) {
			dto.administrativeZoneRefs = part.getAdministrativeZones().stream().map(az -> az.getId()).collect(Collectors.toList());
		}

		return dto;
	}

	private OrganisationPart fromDTO(OrganisationPartDTO dto, CodeSpace codeSpace) {
		OrganisationPart entity = new OrganisationPart();

		entity.setCodeSpace(codeSpace);
		entity.setPrivateCode(UUID.randomUUID().toString());

		return fromDTO(dto, entity);
	}

	private OrganisationPart fromDTO(OrganisationPartDTO dto, OrganisationPart entity) {
		entity.setName(dto.name);
		if (!CollectionUtils.isEmpty(dto.administrativeZoneRefs)) {
			entity.setAdministrativeZones(dto.administrativeZoneRefs.stream().map(az -> administrativeZoneRepository.getOneByPublicId(az)).collect(Collectors.toSet()));
		} else {
			entity.setAdministrativeZones(new HashSet<>());
		}
		return entity;
	}

	private Organisation createByType(OrganisationDTO.OrganisationType type) {
		switch (type) {
			case AUTHORITY:
				return new Authority();
		}
		throw new BadRequestException("Unknown organisation type:" + type);
	}
}
