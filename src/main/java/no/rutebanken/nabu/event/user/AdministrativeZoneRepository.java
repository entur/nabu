package no.rutebanken.nabu.event.user;

import no.rutebanken.nabu.event.user.model.AdministrativeZone;

public interface AdministrativeZoneRepository {

    AdministrativeZone getAdministrativeZone(String id);
}
