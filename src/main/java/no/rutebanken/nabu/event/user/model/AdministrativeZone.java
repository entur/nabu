package no.rutebanken.nabu.event.user.model;

import com.vividsolutions.jts.geom.Polygon;

public class AdministrativeZone {

    private String id;

    private String name;

    private Polygon polygon;

    public AdministrativeZone(String id, String name, Polygon polygon) {
        this.id = id;
        this.name = name;
        this.polygon = polygon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Polygon getPolygon() {
        return polygon;
    }
}
