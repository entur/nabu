package no.rutebanken.nabu.organisation.model.organisation;

import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

	@NotNull
	private AdministrativeZoneType administrativeZoneType;

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


	public AdministrativeZoneType getAdministrativeZoneType() {
		return administrativeZoneType;
	}

	public void setAdministrativeZoneType(AdministrativeZoneType administrativeZoneType) {
		this.administrativeZoneType = administrativeZoneType;
	}
}
