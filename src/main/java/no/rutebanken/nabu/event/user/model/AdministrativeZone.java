/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.event.user.model;


import org.locationtech.jts.geom.Polygon;

public class AdministrativeZone {

    private final String id;

    private final String name;

    private final Polygon polygon;

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
