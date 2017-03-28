package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.responsibility.EntityType;
import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.EntityTypeDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class EntityTypeValidator implements DTOValidator<EntityType, EntityTypeDTO> {

	@Override
	public void validateCreate(EntityTypeDTO dto) {
		Assert.hasLength(dto.privateCode, "privateCode required");
		assertCommon(dto);
	}

	@Override
	public void validateUpdate(EntityTypeDTO dto, EntityType entity) {
		assertCommon(dto);
	}

	private void assertCommon(EntityTypeDTO dto) {
		Assert.hasLength(dto.name, "name required");

		if (dto.classifications != null) {
			for (TypeDTO classification : dto.classifications) {
				Assert.hasLength(classification.name, "classifications.name required");
				if (classification.id == null) {
					Assert.hasLength(classification.privateCode, "classifications.privateCode or classifications.id required");
				}
			}
		}
	}
}
