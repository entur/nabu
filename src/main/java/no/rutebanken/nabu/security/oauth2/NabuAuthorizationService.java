package no.rutebanken.nabu.security.oauth2;

import org.rutebanken.helper.organisation.authorization.AuthorizationService;

public interface NabuAuthorizationService extends AuthorizationService<Long> {

    /**
     * Whether the current user can edit route data belonging to a given codespace.
     */
    boolean canViewTimetableDataEvent(String codespace);
}
