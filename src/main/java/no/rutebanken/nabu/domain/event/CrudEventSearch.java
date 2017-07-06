package no.rutebanken.nabu.domain.event;

import javax.ws.rs.QueryParam;
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