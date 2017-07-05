package no.rutebanken.nabu.organisation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.organisation.model.OrganisationException;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassificationAssignment;
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
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeycloakIamService implements IamService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${iam.keycloak.integration.enabled:true}")
    private boolean enabled;

    @Value("#{'${iam.keycloak.default.roles:rutebanken}'.split(',')}")
    private List<String> defaultRoles;

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

        try {
            iamRealm.roles().create(toKeycloakRole(role));
        } catch (Exception e) {
            String msg = "Keycloak createRole failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

        logger.info("Role successfully created in Keycloak: " + role.getId());
    }

    @Override
    public void removeRole(Role role) {
        if (!enabled) {
            logger.info("Keycloak disabled! Ignored removeRole: " + role.getId());
            return;
        }

        try {
            iamRealm.roles().get(role.getId()).remove();
            logger.info("Role successfully removed from Keycloak: " + role.getId());
        } catch (NotFoundException nfe) {
            logger.info("Ignoring removeRole for role not found in Keycloak: " + role.getId());
        } catch (Exception e) {
            String msg = "Keycloak removeRole failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

    }

    public void createUser(User user) {
        if (!enabled) {
            logger.info("Keycloak disabled! Ignored createUser: " + user.getUsername());
            return;
        }
        Response rsp = iamRealm.users().create(toKeycloakUser(user));
        if (rsp.getStatus() >= 300) {
            String msg = "Failed to create user in Keycloak";
            if (rsp.getEntity() instanceof String) {
                msg += ": " + rsp.getEntity();
            }
            throw new OrganisationException(msg, rsp.getStatus());
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

        try {
            updateUser(user, roleRepository.findAll());
        } catch (Exception e) {
            String msg = "Keycloak updateUser failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }
    }

    @Override
    public void resetPassword(User user) {
        try {
            resetPassword(user.getUsername());
        } catch (Exception e) {
            String msg = "Keycloak resetPassword failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }
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

        try {
            UserResource iamUser = getUserResourceByUsername(user.getUsername());
            iamUser.remove();
            logger.info("User successfully removed from Keycloak: " + user.getUsername());
        } catch (NotFoundException nfe) {
            logger.info("Ignoring removeUser for user not found in Keycloak: " + user.getUsername());
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().startsWith("Username not found")) {
                logger.info("Ignoring removeUser for user not found in Keycloak: " + user.getUsername());
            } else {
                String msg = "Keycloak removeUser failed: " + e.getMessage();
                logger.info(msg, e);
                throw new OrganisationException(msg);
            }
        }
    }

    @Override
    public void updateResponsibilitySet(ResponsibilitySet responsibilitySet) {
        if (!enabled) {
            logger.info("Keycloak disabled! Ignored updateResponsibilitySet: " + responsibilitySet.getName());
            return;
        }

        List<Role> systemRoles = roleRepository.findAll();

        try {
            userRepository.findUsersWithResponsibilitySet(responsibilitySet).forEach(u -> updateUser(u, systemRoles));
        } catch (Exception e) {
            String msg = "Keycloak updateResponsibilitySet failed: " + e.getMessage();
            logger.info(msg, e);
            throw new OrganisationException(msg);
        }

    }

    // Credentials may not be set when creating a user
    private void resetPassword(String username) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(generatePassword());
        credential.setTemporary(Boolean.TRUE);
        getUserResourceByUsername(username).resetPassword(credential);
    }

    private Set<String> getRoleNames(User user) {
        Set<String> roleNames = new HashSet<>(defaultRoles);
        for (ResponsibilitySet responsibilitySet : user.getResponsibilitySets()) {
            roleNames.addAll(responsibilitySet.getRoles().stream().map(r -> r.getTypeOfResponsibilityRole().getId()).collect(Collectors.toSet()));
        }
        return roleNames;
    }

    String generatePassword() {
        List<CharacterRule> rules = Arrays.asList(
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1));
        return new PasswordGenerator().generatePassword(12, rules);
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
        List<UserRepresentation> matchingUserRepresentations = iamRealm.users().search(username, null, null, null, 0, 50);

        List<UserRepresentation> userRepresentations = matchingUserRepresentations.stream().filter(ur -> username.equals(ur.getUsername())).collect(Collectors.toList());

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
        RoleAssignment atr = toRoleAssignment(roleAssignment);

        try {
            ObjectMapper mapper = new ObjectMapper();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, atr);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected RoleAssignment toRoleAssignment(ResponsibilityRoleAssignment roleAssignment) {
        RoleAssignment atr = new RoleAssignment();
        atr.r = roleAssignment.getTypeOfResponsibilityRole().getPrivateCode();
        atr.o = roleAssignment.getResponsibleOrganisation().getPrivateCode();

        if (roleAssignment.getResponsibleArea() != null) {
            atr.z = roleAssignment.getResponsibleArea().getPrivateCode();
        }

        if (!CollectionUtils.isEmpty(roleAssignment.getResponsibleEntityClassifications())) {
            roleAssignment.getResponsibleEntityClassifications().forEach(ec -> addEntityClassification(atr, ec));
        }
        return atr;
    }


    private void addEntityClassification(RoleAssignment atr, EntityClassificationAssignment entityClassificationAssignment) {
        if (atr.e == null) {
            atr.e = new HashMap<>();
        }


        String entityTypeRef = entityClassificationAssignment.getEntityClassification().getEntityType().getPrivateCode();
        List<String> entityClassificationsForEntityType = atr.e.get(entityTypeRef);
        if (entityClassificationsForEntityType == null) {
            entityClassificationsForEntityType = new ArrayList<>();
            atr.e.put(entityTypeRef, entityClassificationsForEntityType);
        }


        // Represented negated entity classifications with '!' prefix for now. consider more structured representation.
        String classifierCode = entityClassificationAssignment.getEntityClassification().getPrivateCode();
        if (!entityClassificationAssignment.isAllow()) {
            classifierCode = "!" + classifierCode;
        }

        entityClassificationsForEntityType.add(classifierCode);
    }


}
