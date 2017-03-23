package no.rutebanken.nabu.organisation.rest.mapper;

import no.rutebanken.nabu.organisation.model.CodeSpace;
import no.rutebanken.nabu.organisation.rest.dto.CodeSpaceDTO;
import org.springframework.stereotype.Service;

@Service
public class CodeSpaceMapper implements DTOMapper<CodeSpace, CodeSpaceDTO> {

	@Override
	public CodeSpace createFromDTO(CodeSpaceDTO dto, Class<CodeSpace> clazz) {
		CodeSpace entity = new CodeSpace();
		entity.setPrivateCode(dto.privateCode);
		entity.setXmlns(dto.xmlns);
		return updateFromDTO(dto, entity);
	}

	@Override
	public CodeSpace updateFromDTO(CodeSpaceDTO dto, CodeSpace entity) {
		entity.setXmlnsUrl(dto.xmlnsUrl);
		return entity;
	}

	@Override
	public CodeSpaceDTO toDTO(CodeSpace entity, boolean fullDetails) {
		return new CodeSpaceDTO(entity.getId(), entity.getXmlns(), entity.getXmlnsUrl());
	}
}
