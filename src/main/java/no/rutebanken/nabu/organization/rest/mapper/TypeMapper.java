package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;
import no.rutebanken.nabu.organization.model.TypeEntity;
import no.rutebanken.nabu.organization.model.VersionedEntity;
import no.rutebanken.nabu.organization.repository.CodeSpaceRepository;
import no.rutebanken.nabu.organization.rest.dto.TypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TypeMapper<R extends VersionedEntity & TypeEntity> implements DTOMapper<R, TypeDTO> {
	@Autowired
	protected CodeSpaceRepository codeSpaceRepository;

	public TypeDTO toDTO(R entity, boolean fullDetails) {
		TypeDTO dto = new TypeDTO();
		dto.name = entity.getName();
		dto.id = entity.getId();
		dto.privateCode = entity.getPrivateCode();
		return dto;
	}

	@Override
	public R createFromDTO(TypeDTO dto, Class<R> clazz) {
		R entity = createInstance(clazz);

		entity.setPrivateCode(dto.privateCode);
		if (entity instanceof CodeSpaceEntity) {
			((CodeSpaceEntity) entity).setCodeSpace(codeSpaceRepository.getOneByPublicId(dto.codeSpace));
		}

		return updateFromDTO(dto, entity);
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
		entity.setName(dto.name);
		return entity;
	}

}
