package no.rutebanken.nabu.organisation.model.responsibility;

import no.rutebanken.nabu.organisation.model.CodeSpace;
import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;
import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(columnNames = {"privateCode", "entityVersion"})
})
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
	@JoinTable(name="RESPONSIBILITY_ROLE_ASSIGNMENT_ENTITY_CLASSIFICATIONS")
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
		if (responsibleEntityClassifications == null) {
			this.responsibleEntityClassifications = new HashSet<>();
		}
		return responsibleEntityClassifications;
	}

	public void setResponsibleEntityClassifications(Set<EntityClassification> responsibleEntityClassifications) {
		getResponsibleEntityClassifications().clear();
		getResponsibleEntityClassifications().addAll(responsibleEntityClassifications);
	}

	public static Builder builder() {
		return new Builder();
	}


	public static class Builder {
		private ResponsibilityRoleAssignment target = new ResponsibilityRoleAssignment();

		public Builder withPrivateCode(String privateCode) {
			target.setPrivateCode(privateCode);
			return this;
		}

		public Builder withCodeSpace(CodeSpace codeSpace) {
			target.setCodeSpace(codeSpace);
			return this;
		}

		public Builder withResponsibleOrganisation(Organisation responsibleOrganisation) {
			target.setResponsibleOrganisation(responsibleOrganisation);
			return this;
		}

		public Builder withResponsibleArea(AdministrativeZone responsibleArea) {
			target.setResponsibleArea(responsibleArea);
			return this;
		}

		public Builder withTypeOfResponsibilityRole(Role typeOfResponsibilityRole) {
			target.setTypeOfResponsibilityRole(typeOfResponsibilityRole);
			return this;
		}

		public Builder withResponsibleEntityClassifications(Set<EntityClassification> responsibleEntityClassifications) {
			target.setResponsibleEntityClassifications(responsibleEntityClassifications);
			return this;
		}


		public ResponsibilityRoleAssignment build() {
			return target;
		}
	}
}


