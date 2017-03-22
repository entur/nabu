package no.rutebanken.nabu.organisation.model.user;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_account", uniqueConstraints = {
		                                                  @UniqueConstraint(columnNames = {"privateCode", "entityVersion"})
})
public class User extends VersionedEntity {

	@NotNull
	private String username;

	@OneToOne
	private ContactDetails contactDetails;

	@NotNull
	@ManyToOne
	private Organisation organisation;

	@OneToMany
	private Set<Notification> notifications;

	@ManyToMany
	private Set<ResponsibilitySet> responsibilitySets;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ContactDetails getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Set<ResponsibilitySet> getResponsibilitySets() {
		if (responsibilitySets == null) {
			this.responsibilitySets = new HashSet<>();
		}
		return responsibilitySets;
	}

	public void setResponsibilitySets(Set<ResponsibilitySet> responsibilitySets) {
		this.responsibilitySets = responsibilitySets;
	}

	@PreRemove
	private void removeResponsibilitySetConnections() {
		if (responsibilitySets != null) {
			responsibilitySets.clear();
		}
	}

	@Override
	public String getId() {
		return Joiner.on(":").join(getType(), getPrivateCode());
	}


	public static User.Builder builder() {
		return new User.Builder();
	}


	public static class Builder {
		private User user = new User();

		public Builder withPrivateCode(String privateCode) {
			user.setPrivateCode(privateCode);
			return this;
		}


		public Builder withUsername(String username) {
			user.setUsername(username);
			return this;
		}

		public Builder withOrganisation(Organisation organisation) {
			user.setOrganisation(organisation);
			return this;
		}

		public Builder withNotifications(Set<Notification> notifications) {
			user.setNotifications(notifications);
			return this;
		}

		public Builder withResponsibilitySets(Set<ResponsibilitySet> responsibilitySets) {
			user.setResponsibilitySets(responsibilitySets);
			return this;
		}

		public Builder withContactDetails(ContactDetails contactDetails) {
			user.setContactDetails(contactDetails);
			return this;
		}

		public User build() {
			return user;
		}
	}
}
