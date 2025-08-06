package no.rutebanken.nabu.event.support;


import javax.annotation.Nullable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateUtils {

    private DateUtils() {
    }

    /**
     * Convert an instant to a zoned date time at the default system timezone.
     */
    @Nullable
    public static ZonedDateTime atDefaultZone(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.systemDefault());
    }

        /**
         * Convert an instant to a zoned date time at the default system zone offset.
         */
        @Nullable
        public static OffsetDateTime atDefaultOffset(Instant instant) {
            if (instant == null) {
                return null;
            }
            return instant.atOffset(ZoneOffset.UTC);
        }
}
