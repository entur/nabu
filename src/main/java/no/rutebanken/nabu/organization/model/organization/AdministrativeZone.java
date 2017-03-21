package no.rutebanken.nabu.organization.model.organization;

import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.organization.model.CodeSpaceEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class AdministrativeZone extends CodeSpaceEntity {

	private String name;

	@Column(columnDefinition = "geometry")
	private Polygon polygon;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
}
