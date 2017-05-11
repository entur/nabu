package no.rutebanken.nabu.domain.event;

import java.util.Arrays;

/**
 * Sub action types for TimeTable job domain.
 */
public enum TimeTableActionSubType {
    FILE_TRANSFER, FILE_CLASSIFICATION, IMPORT, EXPORT, VALIDATION_LEVEL_1, VALIDATION_LEVEL_2, CLEAN, DATASPACE_TRANSFER, BUILD_GRAPH, EXPORT_NETEX;


    /**
     * Return corresponding event action for this sub action.
     */
    public EventAction getEventAction() {

        if (Arrays.asList(TimeTableActionSubType.FILE_CLASSIFICATION, TimeTableActionSubType.FILE_TRANSFER, TimeTableActionSubType.IMPORT, TimeTableActionSubType.DATASPACE_TRANSFER, TimeTableActionSubType.VALIDATION_LEVEL_1).contains(this)) {
            return EventAction.IMPORT;
        }
        return EventAction.EXPORT;
    }
}
