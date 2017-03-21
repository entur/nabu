package no.rutebanken.nabu.organization.model.responsibility;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;
import no.rutebanken.nabu.organization.model.TypeEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class EntityClassification extends CodeSpaceEntity implements TypeEntity {

	@NotNull
	@ManyToOne
	private EntityType entityType;

	@NotNull
	private String name;
	private String privateCode;

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

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
