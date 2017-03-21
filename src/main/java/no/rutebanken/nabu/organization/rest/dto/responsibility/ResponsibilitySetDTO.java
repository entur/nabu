package no.rutebanken.nabu.organization.rest.dto.responsibility;

import no.rutebanken.nabu.organization.rest.dto.BaseDTO;

import java.util.List;

public class ResponsibilitySetDTO extends BaseDTO {

	public String name;

	public List<ResponsibilityRoleAssignmentDTO> roles;
}
