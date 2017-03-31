package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AdministrativeZoneValidator implements DTOValidator<AdministrativeZone, AdministrativeZoneDTO> {

	@Override
	public void validateCreate(AdministrativeZoneDTO dto) {
		Assert.hasLength(dto.privateCode, "privateCode required");
		Assert.hasLength(dto.codeSpace, "codeSpace required");
		Assert.hasLength(dto.name, "name required");
		Assert.notNull(dto.polygon,"polygon required");
	}

	@Override
	public void validateUpdate(AdministrativeZoneDTO dto, AdministrativeZone entity) {
		Assert.hasLength(dto.name, "name required");
		Assert.notNull(dto.polygon,"polygon required");
	}
}
