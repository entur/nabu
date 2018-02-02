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

package no.rutebanken.nabu.event.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.event.user.dto.organisation.AdministrativeZoneDTO;
import no.rutebanken.nabu.event.user.model.AdministrativeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.annotation.PostConstruct;

@Service
public class AdministrativeZoneCache implements AdministrativeZoneRepository {
    private GeoJSONReader reader = new GeoJSONReader();

    @Autowired
    private AdministrativeZoneResource administrativeZoneResource;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${administrative.zone.cache.max.size:100}")
    private Integer cacheMaxSize;


    private Cache<String, AdministrativeZone> cache;

    @PostConstruct
    void init() {
        cache = CacheBuilder.newBuilder().maximumSize(cacheMaxSize).build();
    }

    @Override
    public AdministrativeZone getAdministrativeZone(String id) {
        AdministrativeZone administrativeZone = cache.getIfPresent(id);

        if (administrativeZone == null) {
            AdministrativeZoneDTO dto = administrativeZoneResource.getAdministrativeZone(id);
            if (dto == null) {
                return null;
            }
            administrativeZone = new AdministrativeZone(dto.id, dto.name, (Polygon) reader.read(dto.polygon));
            cache.put(id, administrativeZone);
        }

        return administrativeZone;
    }


}
