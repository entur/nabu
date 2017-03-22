package no.rutebanken.nabu.organization.model.responsibility;

import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.CodeSpaceEntity;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(columnNames = {"privateCode", "entityVersion"})
})
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
		if (roles == null) {
			this.roles = new HashSet<>();
		}
		return roles;
	}

	public void setRoles(Set<ResponsibilityRoleAssignment> roles) {
		this.roles = roles;
	}

	public ResponsibilitySet() {
	}

	public ResponsibilitySet(CodeSpace codeSpace, String privateCode, String name, Set<ResponsibilityRoleAssignment> roles) {
		setCodeSpace(codeSpace);
		setPrivateCode(privateCode);
		this.name = name;
		this.roles = roles;
	}

	public ResponsibilityRoleAssignment getResponsibilityRoleAssignment(String id) {
		if (id != null && !CollectionUtils.isEmpty(roles)) {
			for (ResponsibilityRoleAssignment existingRole : roles) {
				if (id.equals(existingRole.getId())) {
					return existingRole;
				}
			}
		}
		throw new EntityNotFoundException(getClass().getSimpleName() + " with id: " + id + " not found");
	}

	@PreRemove
	private void removeResponsibilitySetConnections() {
		if (roles != null) {
			roles.clear();
		}
	}


}
