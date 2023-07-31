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

package no.rutebanken.nabu.domain.event;

import jakarta.ws.rs.QueryParam;
import java.time.Instant;
import java.util.Date;

public class CrudEventSearch {
    @QueryParam(value = "username")
    private String username;
    @QueryParam(value = "entityType")
    private String entityType;
    @QueryParam(value = "entityClassifier")
    private String entityClassifier;
    @QueryParam(value = "action")
    private String action;
    @QueryParam(value = "externalId")
    private String externalId;
    @QueryParam(value = "from")
    private Date from;
    @QueryParam(value = "to")
    private Date to;

    public CrudEventSearch() {
    }

    public CrudEventSearch(String username, String entityType, String entityClassifier, String action, String externalId, Instant from, Instant to) {
        this.username = username;
        this.entityType = entityType;
        this.entityClassifier = entityClassifier;
        this.action = action;
        this.externalId = externalId;
        this.from = from == null ? null : Date.from(from);
        this.to = to == null ? null : Date.from(to);
    }


    public String getUsername() {
        return username;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityClassifier() {
        return entityClassifier;
    }

    public String getAction() {
        return action;
    }

    public String getExternalId() {
        return externalId;
    }

    public Instant getFrom() {
        return from == null ? null : from.toInstant();
    }

    public Instant getTo() {
        return to == null ? null : to.toInstant();
    }
}