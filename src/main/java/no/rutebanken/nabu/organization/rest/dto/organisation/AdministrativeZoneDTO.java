package no.rutebanken.nabu.organization.rest.dto.organisation;


import no.rutebanken.nabu.organization.rest.dto.BaseDTO;
import org.wololo.geojson.Polygon;

public class AdministrativeZoneDTO extends BaseDTO {

	public String name;

	public Polygon polygon;
}
