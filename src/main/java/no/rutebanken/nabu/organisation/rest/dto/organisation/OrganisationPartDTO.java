package no.rutebanken.nabu.organisation.rest.dto.organisation;

import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;

import java.util.ArrayList;
import java.util.List;

public class OrganisationPartDTO extends BaseDTO {

	public String name;

	public List<String> administrativeZoneRefs = new ArrayList<>();
}
