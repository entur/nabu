package no.rutebanken.nabu.organisation.model.user;

public enum NotificationType {
    EMAIL, WEB, EMAIL_BATCH;

    /**
     * fica
     * Whether or not notifications of this type should be sent immediately or stored for batch notification.
     *
     */
    public boolean isImmediate() {
        return EMAIL.equals(this);
    }
}
