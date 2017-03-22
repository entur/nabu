package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.organization.Authority;
import no.rutebanken.nabu.organization.model.organization.Organisation;
import no.rutebanken.nabu.organization.model.organization.OrganisationPart;
import no.rutebanken.nabu.organization.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organization.repository.CodeSpaceRepository;
import no.rutebanken.nabu.organization.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organization.rest.dto.organisation.OrganisationPartDTO;
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

	public OrganisationDTO toDTO(Organisation organisation, boolean fullDetails) {
		OrganisationDTO dto = new OrganisationDTO();
		dto.id = organisation.getId();

		dto.companyNumber = organisation.getCompanyNumber();
		dto.name = organisation.getName();

		if (organisation instanceof Authority) {
			dto.organisationType = OrganisationDTO.OrganisationType.AUTHORITY;
		}

		if (!CollectionUtils.isEmpty(organisation.getParts())) {
			dto.parts = organisation.getParts().stream().map(p -> toDTO(p)).collect(Collectors.toList());
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

		Set<OrganisationPart> parts = new HashSet<>();

		if (dto.parts != null) {
			for (OrganisationPartDTO dtoPart : dto.parts) {
				OrganisationPart part = entity.getOrganisationPart(dtoPart.id);
				if (part == null) {
					part = fromDTO(dtoPart, entity.getCodeSpace());
				} else {
					part = fromDTO(dtoPart, part);
				}
				parts.add(part);
			}
		}

		entity.replaceParts(parts);

		return entity;
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
			entity.replaceAdministrativeZones(dto.administrativeZoneRefs.stream().map(az -> administrativeZoneRepository.getOneByPublicId(az)).collect(Collectors.toSet()));
		} else {
			entity.replaceAdministrativeZones(new HashSet<>());
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
