package no.rutebanken.nabu.organisation.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.model.responsibility.Role;
import no.rutebanken.nabu.organisation.model.user.User;
import no.rutebanken.nabu.organisation.repository.RoleRepository;
import no.rutebanken.nabu.organisation.repository.UserRepository;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
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
import java.util.stream.Collectors;

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

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void createRole(Role role) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored createRole: " + role.getId());
			return;
		}
		iamRealm.roles().create(toKeycloakRole(role));

		logger.info("Role successfully created in Keycloak: " + role.getId());
	}

	@Override
	public void removeRole(Role role) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored removeRole: " + role.getId());
			return;
		}

		iamRealm.roles().get(role.getId()).remove();

		logger.info("Role successfully removed from Keycloak: " + role.getId());
	}

	public void createUser(User user) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored createUser: " + user.getUsername());
			return;
		}
		Response rsp = iamRealm.users().create(toKeycloakUser(user));
		if (rsp.getStatus() >= 300) {
			throw new WebApplicationException("Failed to create user in Keycloak", rsp);
		}

		try {
			resetPassword(user.getUsername());
			updateRoles(user, roleRepository.findAll());
		} catch (Exception e) {
			logger.info("Password or role assignment failed for new Keycloak user. Attempting to remove user");
			removeUser(user);
			throw e;
		}

		logger.info("User successfully created in Keycloak: " + user.getUsername());
	}

	public void updateUser(User user) {
		updateUser(user, roleRepository.findAll());
	}

	private void updateUser(User user, List<Role> systemRoles) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored updateUser: " + user.getUsername());
			return;
		}

		UserResource iamUser = getUserResourceByUsername(user.getUsername());
		iamUser.update(toKeycloakUser(user));
		updateRoles(user, systemRoles);

		logger.info("User successfully updated in Keycloak: " + user.getUsername());
	}

	public void removeUser(User user) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored removeUser: " + user.getUsername());
			return;
		}

		UserResource iamUser = getUserResourceByUsername(user.getUsername());
		iamUser.remove();

		logger.info("User successfully removed from Keycloak: " + user.getUsername());
	}

	@Override
	public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {
		if (!enabled) {
			logger.info("Keycloak disabled! Ignored updateResponsibilitySet: " + responsibilitySet.getName());
			return;
		}

		List<Role> systemRoles = roleRepository.findAll();
		userRepository.findUsersWithResponsibilitySet(responsibilitySet).forEach(u -> updateUser(u, systemRoles));
	}

	// Credentials may not be set when creating a user
	private void resetPassword(String username) {
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(defaultPassword);
		credential.setTemporary(Boolean.TRUE);
		getUserResourceByUsername(username).resetPassword(credential);
	}

	private Set<String> getRoleNames(User user) {
		Set<String> roleNames = new HashSet<>();
		for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
			roleNames.addAll(responsibilitySet.getRoles().stream().map(r -> r.getTypeOfResponsibilityRole().getId()).collect(Collectors.toSet()));
		}
		return roleNames;
	}


	private void updateRoles(User user, List<Role> systemRoles) {
		Set<String> customRoleNames = systemRoles.stream().map(r -> r.getId()).collect(Collectors.toSet());

		List<RoleRepresentation> existingRoles = getUserResourceByUsername(user.getUsername()).roles().realmLevel().listEffective();

		Set<String> newRoleNames = getRoleNames(user);

		List<RoleRepresentation> removeRoles = existingRoles.stream().filter(r -> customRoleNames.contains(r.getName()))
				                                       .filter(r -> !newRoleNames.remove(r)).collect(Collectors.toList());

		if (!removeRoles.isEmpty()) {
			getUserResourceByUsername(user.getUsername()).roles().realmLevel().remove(removeRoles);
		}
		if (!newRoleNames.isEmpty()) {
			List<RoleRepresentation> newRole = newRoleNames.stream().map(rn -> iamRealm.roles().get(rn).toRepresentation()).collect(Collectors.toList());
			getUserResourceByUsername(user.getUsername()).roles().realmLevel().add(newRole);
		}

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

	RoleRepresentation toKeycloakRole(Role role) {
		RoleRepresentation roleRepresentation = new RoleRepresentation();
		roleRepresentation.setName(role.getId());
		return roleRepresentation;
	}

	UserRepresentation toKeycloakUser(User user) {
		UserRepresentation kcUser = new UserRepresentation();

		kcUser.setEnabled(true);
		kcUser.setUsername(user.getUsername());

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
