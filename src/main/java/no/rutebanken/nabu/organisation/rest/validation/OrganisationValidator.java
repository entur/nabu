package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationPartDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class OrganisationValidator implements DTOValidator<Organisation, OrganisationDTO> {

	@Override
	public void validateCreate(OrganisationDTO dto) {
		Assert.hasLength(dto.privateCode, "privateCode required");
		Assert.hasLength(dto.codeSpace, "codeSpace required");
		Assert.hasLength(dto.name, "name required");
		Assert.notNull(dto.organisationType, "organisationType required");

		if (dto.parts != null) {
			dto.parts.forEach(p -> validatePart(p));
		}

	}

	private void validatePart(OrganisationPartDTO dto) {
		Assert.hasLength(dto.name, "parts.name required");
	}

	@Override
	public void validateUpdate(OrganisationDTO dto, Organisation entity) {
		Assert.hasLength(dto.name, "name required");
		if (dto.parts != null) {
			dto.parts.forEach(p -> validatePart(p));
		}
	}
}
