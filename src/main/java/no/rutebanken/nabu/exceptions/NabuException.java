package no.rutebanken.nabu.exceptions;

public class NabuException extends RuntimeException {


    public NabuException(String message) {
        super(message);
    }

    public NabuException(String message, Throwable cause) {
        super(message, cause);
    }
}
