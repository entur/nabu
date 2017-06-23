package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;

public interface DTOValidator<E extends VersionedEntity, D extends BaseDTO> {

	void validateCreate(D dto);

	void validateUpdate(D dto, E entity);

	void validateDelete(E entity);
}
