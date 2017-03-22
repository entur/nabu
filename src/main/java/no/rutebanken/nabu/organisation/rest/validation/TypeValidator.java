package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TypeValidator<E extends VersionedEntity> implements DTOValidator<E, TypeDTO> {
	@Override
	public void validateCreate(TypeDTO dto) {
		Assert.hasLength(dto.name, "name required");
		Assert.hasLength(dto.privateCode, "privateCode required");
	}

	@Override
	public void validateUpdate(TypeDTO dto, E entity) {
		Assert.hasLength(dto.name, "name required");
	}
}
