package no.rutebanken.nabu.organisation.rest.dto.responsibility;

import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;

import java.util.List;

public class ResponsibilityRoleAssignmentDTO extends BaseDTO {

	public String typeOfResponsibilityRoleRef;
	public String responsibleOrganisationRef;
	public String responsibleAreaRef;

	public List<String> entityClassificationRefs;
}
