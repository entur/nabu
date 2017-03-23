package no.rutebanken.nabu.organisation.model.responsibility;

import no.rutebanken.nabu.organisation.model.TypeEntity;
import no.rutebanken.nabu.organisation.model.VersionedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(name = "role_unique_id", columnNames = {"privateCode", "entityVersion"})
})
public class Role extends VersionedEntity implements TypeEntity {

	@NotNull
	private String name;

	public Role() {
	}

	public Role(String privateCode, String name) {
		setPrivateCode(privateCode);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
