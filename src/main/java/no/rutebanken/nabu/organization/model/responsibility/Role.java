package no.rutebanken.nabu.organization.model.responsibility;

import no.rutebanken.nabu.organization.model.TypeEntity;
import no.rutebanken.nabu.organization.model.VersionedEntity;

import javax.persistence.Entity;

@Entity
public class Role extends VersionedEntity implements TypeEntity {

	private String name;

	private String privateCode;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getPrivateCode() {
		return privateCode;
	}

	@Override
	public void setPrivateCode(String privateCode) {
		this.privateCode = privateCode;
	}
}
