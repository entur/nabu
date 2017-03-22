package no.rutebanken.nabu.organisation.model.organisation;

import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;
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
		if (parts == null) {
			this.parts = new HashSet<>();
		}
		return parts;
	}

	public void setParts(Set<OrganisationPart> parts) {
		getParts().clear();
		getParts().addAll(parts);
	}


	public OrganisationPart getOrganisationPart(String id) {
		if (id != null && !CollectionUtils.isEmpty(parts)) {
			for (OrganisationPart existingPart : parts) {
				if (id.equals(existingPart.getId())) {
					return existingPart;
				}
			}
		}
		throw new ValidationException(getClass().getSimpleName() + " with id: " + id + " not found");
	}

}
