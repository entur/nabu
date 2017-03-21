package no.rutebanken.nabu.organization.rest.mapper;

import no.rutebanken.nabu.organization.model.VersionedEntity;
import no.rutebanken.nabu.organization.rest.dto.BaseDTO;

public interface DTOMapper<E extends VersionedEntity, D extends BaseDTO> {

	E createFromDTO(D dto, Class<E> clazz);

	E updateFromDTO(D dto, E entity);

	D toDTO(E entity);

}
