package no.rutebanken.nabu.organization.model.responsibility;

import no.rutebanken.nabu.organization.model.TypeEntity;
import no.rutebanken.nabu.organization.model.VersionedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(columnNames = {"privateCode", "entityVersion"})
})
public class Role extends VersionedEntity implements TypeEntity {

	@NotNull
	private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
