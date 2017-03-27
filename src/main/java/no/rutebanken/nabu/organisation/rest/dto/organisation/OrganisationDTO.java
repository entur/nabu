package no.rutebanken.nabu.organisation.rest.dto.organisation;

import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;

import java.util.ArrayList;
import java.util.List;

public class OrganisationDTO extends BaseDTO {

	public enum OrganisationType {AUTHORITY}

	public String name;

	public Long companyNumber;

	public OrganisationType organisationType;

	public List<OrganisationPartDTO> parts = new ArrayList<>();

}
