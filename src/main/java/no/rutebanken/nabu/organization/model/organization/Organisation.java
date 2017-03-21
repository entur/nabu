package no.rutebanken.nabu.organization.model.organization;

import no.rutebanken.nabu.organization.model.CodeSpaceEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public abstract class Organisation extends CodeSpaceEntity {

	private Long companyNumber;

	private String name;

	@OneToMany
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
}
