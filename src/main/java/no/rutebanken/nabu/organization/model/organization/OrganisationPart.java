package no.rutebanken.nabu.organization.model.organization;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(columnNames = {"privateCode", "entityVersion"})
})
public class OrganisationPart extends CodeSpaceEntity {

	@NotNull
	private String name;

	@ManyToMany
	private Set<AdministrativeZone> administrativeZones;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<AdministrativeZone> getAdministrativeZones() {
		return administrativeZones;
	}

	public void setAdministrativeZones(Set<AdministrativeZone> administrativeZones) {
		this.administrativeZones = administrativeZones;
	}

	@PreRemove
	private void removeResponsibilitySetConnections() {
		if (administrativeZones != null) {
			administrativeZones.clear();
		}
	}

	public void replaceAdministrativeZones(Set<AdministrativeZone> newAdministrativeZones) {
		if (this.administrativeZones == null) {
			this.administrativeZones = newAdministrativeZones;
		} else {
			administrativeZones.clear();
			administrativeZones.addAll(newAdministrativeZones);
		}
	}
}
