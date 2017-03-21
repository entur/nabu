package no.rutebanken.nabu.organization.model.responsibility;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class ResponsibilitySet extends CodeSpaceEntity {

	@NotNull
	private String name;

	@NotNull
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<ResponsibilityRoleAssignment> roles;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ResponsibilityRoleAssignment> getRoles() {
		return roles;
	}

	public void setRoles(Set<ResponsibilityRoleAssignment> roles) {
		this.roles = roles;
	}
}
