package no.rutebanken.nabu.organisation.rest.dto.user;

public class EventFilterDTO {

    public enum EventFilterType {JOB, CRUD};

    public EventFilterType type;

    public EventFilterDTO(EventFilterType type) {
        this.type = type;
    }

    public EventFilterDTO() {
    }
}
