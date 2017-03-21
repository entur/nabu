package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;
import no.rutebanken.nabu.organization.model.VersionedEntity;
import no.rutebanken.nabu.organization.repository.CodeSpaceRepository;
import no.rutebanken.nabu.organization.rest.dto.BaseDTO;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDTOMapper<E extends VersionedEntity, D extends BaseDTO> implements DTOMapper<E, D> {

	@Autowired
	protected CodeSpaceRepository codeSpaceRepository;


	protected E fromDTOBasics(E org, D dto) {
		if (org instanceof CodeSpaceEntity) {
			((CodeSpaceEntity) org).setCodeSpace(codeSpaceRepository.getOneByPublicId(dto.codeSpace));
		}

		org.setPrivateCode(dto.privateCode);
		return org;
	}

	protected D toDTOBasics(E org, D dto) {
		dto.id = org.getId();
		dto.privateCode = org.getPrivateCode();
		return dto;
	}
}
