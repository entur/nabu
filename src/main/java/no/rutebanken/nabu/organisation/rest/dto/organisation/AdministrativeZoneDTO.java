package no.rutebanken.nabu.organisation.rest.dto.organisation;


import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZoneType;
import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;
import org.wololo.geojson.Polygon;

public class AdministrativeZoneDTO extends BaseDTO {

	public String name;

	public Polygon polygon;

	public AdministrativeZoneType type;
}
