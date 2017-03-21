package no.rutebanken.nabu.organization.model.responsibility;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;
import no.rutebanken.nabu.organization.model.organization.AdministrativeZone;
import no.rutebanken.nabu.organization.model.organization.Organisation;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class ResponsibilityRoleAssignment extends CodeSpaceEntity {

	@NotNull
	@ManyToOne
	private Role typeOfResponsibilityRole;

	@NotNull
	@ManyToOne
	private Organisation responsibleOrganisation;

	@ManyToOne
	private AdministrativeZone responsibleArea;


	@ManyToMany
	private Set<EntityClassification> responsibleEntityClassifications;


	public Role getTypeOfResponsibilityRole() {
		return typeOfResponsibilityRole;
	}

	public void setTypeOfResponsibilityRole(Role typeOfResponsibilityRole) {
		this.typeOfResponsibilityRole = typeOfResponsibilityRole;
	}

	public Organisation getResponsibleOrganisation() {
		return responsibleOrganisation;
	}

	public void setResponsibleOrganisation(Organisation responsibleOrganisation) {
		this.responsibleOrganisation = responsibleOrganisation;
	}

	public AdministrativeZone getResponsibleArea() {
		return responsibleArea;
	}

	public void setResponsibleArea(AdministrativeZone responsibleArea) {
		this.responsibleArea = responsibleArea;
	}

	public Set<EntityClassification> getResponsibleEntityClassifications() {
		return responsibleEntityClassifications;
	}

	public void setResponsibleEntityClassifications(Set<EntityClassification> responsibleEntityClassifications) {
		this.responsibleEntityClassifications = responsibleEntityClassifications;
	}
}


