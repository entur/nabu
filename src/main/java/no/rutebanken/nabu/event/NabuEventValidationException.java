package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.exceptions.NabuException;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class NabuEventValidationException extends NabuException {

    private final Set<ConstraintViolation<Event>> validationErrors;

    public NabuEventValidationException(String message, Set<ConstraintViolation<Event>> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public Set<ConstraintViolation<Event>> validationErrors() {
        return validationErrors;
    }
}
