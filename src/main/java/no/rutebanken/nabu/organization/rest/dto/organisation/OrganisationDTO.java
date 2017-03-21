package no.rutebanken.nabu.organization.rest.dto.organisation;

import no.rutebanken.nabu.organization.rest.dto.BaseDTO;

import java.util.List;

public class OrganisationDTO extends BaseDTO {

	public enum OrganisationType {AUTHORITY}

	public String name;

	public Long companyNumber;

	public OrganisationType organisationType;

	public List<OrganisationPartDTO> parts;

}
