package no.rutebanken.nabu.organisation.model.organisation;

import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Wrapper class to enable lazy loading of polygons.
 */
@Entity
public class PersistablePolygon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "geometry")
    @NotNull
    private Polygon polygon;

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public PersistablePolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public PersistablePolygon() {
    }
}
