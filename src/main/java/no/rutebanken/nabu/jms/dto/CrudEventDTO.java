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

package no.rutebanken.nabu.jms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.rutebanken.nabu.domain.event.JobState;
import org.wololo.geojson.Geometry;

import java.io.IOException;
import java.time.Instant;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrudEventDTO {

    public Instant eventTime;

    public String correlationId;

    public String entityType;

    public String entityClassifier;

    public String action;

    public String externalId;

    public Long version;

    public String name;

    public String changeType;

    public String oldValue;

    public String newValue;

    public String comment;

    public String username;

    public Geometry geometry;

    public String location;

    public static CrudEventDTO fromString(String string) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(string, CrudEventDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
