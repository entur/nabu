package no.rutebanken.nabu.organization.model.organization;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilityRoleAssignment;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(columnNames = {"privateCode", "entityVersion"})
})
public abstract class Organisation extends CodeSpaceEntity {

	private Long companyNumber;

	@NotNull
	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	private Set<OrganisationPart> parts;

	public Long getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(Long companyNumber) {
		this.companyNumber = companyNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<OrganisationPart> getParts() {
		return parts;
	}

	public void setParts(Set<OrganisationPart> parts) {
		this.parts = parts;
	}

	public void replaceParts(Set<OrganisationPart> newParts) {
		if (this.parts == null) {
			this.parts = newParts;
		} else {
			parts.clear();
			parts.addAll(newParts);
		}
	}

	public OrganisationPart getOrganisationPart(String id) {
		if (id != null && !CollectionUtils.isEmpty(parts)) {
			for (OrganisationPart existingPart : parts) {
				if (id.equals(existingPart.getId())) {
					return existingPart;
				}
			}
		}
		return null;
	}

}
