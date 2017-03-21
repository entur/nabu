package no.rutebanken.nabu.organization.model.responsibility;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;
import no.rutebanken.nabu.organization.model.TypeEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class EntityType extends CodeSpaceEntity implements TypeEntity {

	@NotNull
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
