package no.rutebanken.nabu.event.user.dto.organisation;



import no.rutebanken.nabu.event.user.dto.BaseDTO;
import org.wololo.geojson.Polygon;

public class AdministrativeZoneDTO extends BaseDTO {

	public String name;

	public String source;

	public Polygon polygon;

}
