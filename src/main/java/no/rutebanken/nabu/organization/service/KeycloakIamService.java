package no.rutebanken.nabu.organization.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.organization.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.model.user.User;
import no.rutebanken.nabu.organization.repository.UserRepository;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Service
public class KeycloakIamService implements IamService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${keycloak.integration.enabled:false}")
	private boolean enabled;

	@Value("${keycloak.default.password:Password123}")
	private String defaultPassword;

	@Autowired
	private RealmResource iamRealm;

	@Autowired
	private UserRepository userRepository;

	public void createUser(User user) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored createUser: " + user.getUsername());
			return;
		}
		Response rsp = iamRealm.users().create(toKeycloakUser(user));
		if (rsp.getStatus() >= 300) {
			throw new WebApplicationException(rsp);
		}
	}

	public void updateUser(User user) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored updateUser: " + user.getUsername());
			return;
		}

		UserResource iamUser = getUserResourceByUsername(user.getUsername());
		iamUser.update(toKeycloakUser(user));
	}

	public void removeUser(User user) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored removeUser: " + user.getUsername());
			return;
		}

		UserResource iamUser = getUserResourceByUsername(user.getUsername());
		iamUser.remove();
	}

	@Override
	public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored updateResponsibilitySet: " + responsibilitySet.getName());
			return;
		}

		userRepository.findUsersWithResponsibilitySet(responsibilitySet).forEach(u -> updateUser(u));
	}


	private UserResource getUserResourceByUsername(String username) {
		List<UserRepresentation> userRepresentations = iamRealm.users().search(username, null, null, null, 0, 2);

		if (userRepresentations.size() == 0) {
			throw new BadRequestException("Username not found in KeyCloak: " + username);
		} else if (userRepresentations.size() > 1) {
			throw new BadRequestException("Username not unique in KeyCloak: " + username);
		}
		return iamRealm.users().get(userRepresentations.get(0).getId());
	}

	UserRepresentation toKeycloakUser(User user) {
		UserRepresentation kcUser = new UserRepresentation();

		kcUser.setEnabled(true);
		kcUser.setUsername(user.getUsername());

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(defaultPassword);
		kcUser.setCredentials(Arrays.asList(credential));

		if (user.getContactDetails() != null) {
			kcUser.setFirstName(user.getContactDetails().getFirstName());
			kcUser.setLastName(user.getContactDetails().getLastName());
			kcUser.setEmail(user.getContactDetails().getEmail());
		}

		if (user.getResponsibilitySets() != null) {
			Map<String, List<String>> attributes = new HashMap<>();
			List<String> roleAssignments = new ArrayList<>();

			for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
				if (responsibilitySet.getRoles() != null) {
					responsibilitySet.getRoles().forEach(rra -> roleAssignments.add(toAtr(rra)));
				}
			}

			attributes.put("roles", roleAssignments);
			kcUser.setAttributes(attributes);
		}

		return kcUser;
	}

	private String toAtr(ResponsibilityRoleAssignment roleAssignment) {
		RoleAssignmentAtr atr = new RoleAssignmentAtr();
		atr.role = roleAssignment.getTypeOfResponsibilityRole().getId();
		atr.org = roleAssignment.getResponsibleOrganisation().getId();

		if (roleAssignment.getResponsibleArea() != null) {
			atr.admZone = roleAssignment.getResponsibleArea().getId();
		}

		if (!CollectionUtils.isEmpty(roleAssignment.getResponsibleEntityClassifications())) {
			roleAssignment.getResponsibleEntityClassifications().forEach(ec -> addEntityClassification(atr, ec));
		}

		return atr.toString();
	}


	private void addEntityClassification(RoleAssignmentAtr atr, EntityClassification entityClassification) {
		if (atr.entities == null) {
			atr.entities = new HashMap<>();
		}

		String entityTypeRef = entityClassification.getEntityType().getId();
		List<String> entityClassificationsForEntityType = atr.entities.get(entityTypeRef);
		if (entityClassificationsForEntityType == null) {
			entityClassificationsForEntityType = new ArrayList<>();
			atr.entities.put(entityTypeRef, entityClassificationsForEntityType);
		}
		entityClassificationsForEntityType.add(entityClassification.getId());
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private class RoleAssignmentAtr {

		public String role;

		public String org;

		public String admZone;

		public Map<String, List<String>> entities;


		public String toString() {
			try {
				ObjectMapper mapper = new ObjectMapper();
				StringWriter writer = new StringWriter();
				mapper.writeValue(writer, this);
				return writer.toString();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
