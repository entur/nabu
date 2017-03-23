package no.rutebanken.nabu.organisation.model.organisation;

import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(name = "org_part_unique_id", columnNames = {"code_space_pk","privateCode", "entityVersion"})
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

		if (administrativeZones == null) {
			administrativeZones = new HashSet<>();
		}
		return administrativeZones;
	}

	public void setAdministrativeZones(Set<AdministrativeZone> administrativeZones) {
		getAdministrativeZones().clear();
		getAdministrativeZones().addAll(administrativeZones);
	}

	@PreRemove
	private void removeResponsibilitySetConnections() {
		if (administrativeZones != null) {
			administrativeZones.clear();
		}
	}

}
