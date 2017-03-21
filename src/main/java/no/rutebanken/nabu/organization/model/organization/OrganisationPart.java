package no.rutebanken.nabu.organization.model.organization;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class OrganisationPart extends CodeSpaceEntity {

	private String name;

	// TODO many to many?
	@OneToMany
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
}
