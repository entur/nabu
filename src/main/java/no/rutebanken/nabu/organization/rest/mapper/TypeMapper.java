package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.TypeEntity;
import no.rutebanken.nabu.organization.model.VersionedEntity;
import no.rutebanken.nabu.organization.model.organization.Organisation;
import no.rutebanken.nabu.organization.rest.dto.TypeDTO;
import org.springframework.stereotype.Service;

@Service
public class TypeMapper<R extends VersionedEntity & TypeEntity> extends BaseDTOMapper<R, TypeDTO> {

	public TypeDTO toDTO(R entity) {
		TypeDTO dto = toDTOBasics(entity, new TypeDTO());
		dto.name = entity.getName();
		dto.privateCode = entity.getPrivateCode();
		return dto;
	}

	@Override
	public R createFromDTO(TypeDTO dto, Class<R> clazz) {
		return updateFromDTO(dto, createInstance(clazz));
	}

	private R createInstance(Class<R> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create instance of class: " + clazz + " : " + e.getMessage());
		}
	}

	@Override
	public R updateFromDTO(TypeDTO dto, R entity) {
		fromDTOBasics(entity, dto);
		entity.setName(dto.name);
		entity.setPrivateCode(dto.privateCode);
		return entity;
	}

}
