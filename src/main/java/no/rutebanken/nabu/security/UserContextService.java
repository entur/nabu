package no.rutebanken.nabu.security;


/**
 * Service returning the privileges of the current user.
 */
public interface UserContextService {

  /**
   * Is the user a route data administrator?
   */
  boolean isRouteDataAdmin();

  /**
   * Is the user an organization administrator?
   */
  boolean isOrganizationAdmin();

  /**
   * Whether the user can edit data for a provider
   * @param providerId The internal code of the provider
   * @return true if the user has access
   */
  boolean canEditProvider(Long providerId);
}
