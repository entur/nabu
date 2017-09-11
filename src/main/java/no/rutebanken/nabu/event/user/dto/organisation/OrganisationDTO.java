package no.rutebanken.nabu.event.user.dto.organisation;


import no.rutebanken.nabu.event.user.dto.BaseDTO;

public class OrganisationDTO extends BaseDTO {

	public enum OrganisationType {AUTHORITY}

	public String name;

	public Long companyNumber;

	public OrganisationType organisationType;

}
