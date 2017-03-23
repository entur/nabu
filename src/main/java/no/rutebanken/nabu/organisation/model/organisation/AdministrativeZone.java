package no.rutebanken.nabu.organisation.model.organisation;

import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(name = "adm_zone_unique_id", columnNames = {"code_space_pk","privateCode", "entityVersion"})
})
public class AdministrativeZone extends CodeSpaceEntity {

	@NotNull
	private String name;

	@Basic(fetch = FetchType.LAZY)
	@Column(columnDefinition = "geometry")
	@NotNull
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
