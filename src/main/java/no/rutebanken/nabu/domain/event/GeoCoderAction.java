package no.rutebanken.nabu.domain.event;

/**
 * Sub action types for GeoCoder job domain.
 */
public enum GeoCoderAction {
    ADDRESS_DOWNLOAD, ADMINISTRATIVE_UNITS_DOWNLOAD,
    PLACE_NAMES_DOWNLOAD, TIAMAT_POI_UPDATE,
    TIAMAT_ADMINISTRATIVE_UNITS_UPDATE,
    TIAMAT_NEIGHBOURING_COUNTRIES_UPDATE, TIAMAT_EXPORT,
    PELIAS_UPDATE;
}
