package no.rutebanken.nabu.organisation.model.responsibility;

import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;
import no.rutebanken.nabu.organisation.model.TypeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(name = "entity_type_unique_id", columnNames = {"code_space_pk","privateCode", "entityVersion"})
})
public class EntityType extends CodeSpaceEntity implements TypeEntity {

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

	@Override
	public String getType() {
		return "TypeOfEntity";
	}

	@OneToMany(mappedBy = "entityType", fetch = FetchType.LAZY)
	private Set<EntityClassification> classifications;

	public Set<EntityClassification> getClassifications() {
		return classifications;
	}

	public void setClassifications(Set<EntityClassification> classifications) {
		this.classifications = classifications;
	}
}
