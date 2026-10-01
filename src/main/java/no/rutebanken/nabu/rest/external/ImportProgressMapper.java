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

package no.rutebanken.nabu.rest.external;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.aggregation.JobEventAggregation;
import no.rutebanken.nabu.rest.openapi.model.ImportProgress;
import no.rutebanken.nabu.rest.openapi.model.ImportProgressStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.rutebanken.nabu.event.support.DateUtils.atDefaultOffset;

/**
 * Turns a correlation's job events into the external per-stage progress view.
 */
final class ImportProgressMapper {

    private static final Logger logger = LoggerFactory.getLogger(ImportProgressMapper.class);

    /**
     * Internal action name to external stage name.
     * <p>
     * Keyed by raw String, deliberately not by {@link no.rutebanken.nabu.domain.event.TimeTableAction}:
     * Event.action is a free-form string written by marduk, and nabu's enum is a copy that has
     * drifted before. Driving this off the enum would silently drop stages marduk emits that nabu
     * does not list. CLEAN is absent because marduk declares it but never emits it.
     */
    private static final Map<String, ImportProgressStage.StageEnum> STAGE_BY_ACTION = Map.ofEntries(
            Map.entry("FILE_TRANSFER", ImportProgressStage.StageEnum.FILE_TRANSFER),
            Map.entry("FILE_CLASSIFICATION", ImportProgressStage.StageEnum.FILE_CLASSIFICATION),
            Map.entry("PREVALIDATION", ImportProgressStage.StageEnum.PREVALIDATION),
            Map.entry("IMPORT", ImportProgressStage.StageEnum.IMPORT),
            Map.entry("VALIDATION_LEVEL_1", ImportProgressStage.StageEnum.VALIDATION_LEVEL_1),
            Map.entry("VALIDATION_LEVEL_2", ImportProgressStage.StageEnum.VALIDATION_LEVEL_2),
            Map.entry("DATASPACE_TRANSFER", ImportProgressStage.StageEnum.DATASPACE_TRANSFER),
            Map.entry("LINKING", ImportProgressStage.StageEnum.LINKING),
            Map.entry("FILTERING", ImportProgressStage.StageEnum.FILTERING),
            Map.entry("EXPORT_NETEX", ImportProgressStage.StageEnum.NETEX_EXPORT),
            Map.entry("EXPORT_NETEX_POSTVALIDATION", ImportProgressStage.StageEnum.POSTVALIDATION),
            Map.entry("EXPORT_NETEX_BLOCKS", ImportProgressStage.StageEnum.BLOCKS_EXPORT),
            Map.entry("EXPORT_NETEX_BLOCKS_POSTVALIDATION", ImportProgressStage.StageEnum.BLOCKS_POSTVALIDATION),
            Map.entry("EXPORT_NETEX_MERGED_POSTVALIDATION", ImportProgressStage.StageEnum.MERGED_POSTVALIDATION),
            Map.entry("EXPORT", ImportProgressStage.StageEnum.GTFS_EXPORT),
            Map.entry("OTP2_BUILD_GRAPH", ImportProgressStage.StageEnum.GRAPH_BUILD));

    /**
     * Internal job state to external status. TIMEOUT folds into FAILED. CANCELLED and DUPLICATE have
     * no external name, so those stages are dropped rather than reported — CANCELLED because it
     * conflates "not run for this provider" with "aborted by a fault" and carries no error code
     * either way, DUPLICATE because it is unreachable for timetables.
     */
    private static final Map<JobState, ImportProgressStage.StatusEnum> STATUS_BY_STATE = Map.of(
            JobState.PENDING, ImportProgressStage.StatusEnum.PENDING,
            JobState.STARTED, ImportProgressStage.StatusEnum.IN_PROGRESS,
            JobState.OK, ImportProgressStage.StatusEnum.COMPLETED,
            JobState.FAILED, ImportProgressStage.StatusEnum.FAILED,
            JobState.TIMEOUT, ImportProgressStage.StatusEnum.FAILED);

    private ImportProgressMapper() {
    }

    /**
     * @param codespace     the codespace resolved from the events, not supplied by the caller
     * @param correlationId the correlation id from the request path
     * @param events        every domain TIMETABLE event for that correlation id, in any order
     */
    static ImportProgress toImportProgress(String codespace, String correlationId, List<JobEvent> events) {
        List<JobEvent> liveEvents = JobEventAggregation.withoutStaleNonTerminalEvents(events);

        // The input is ordered by event time and LinkedHashMap preserves first-encounter order, so
        // the groups already come out ordered by each action's earliest event — the specified order.
        Map<String, List<JobEvent>> eventsByAction = liveEvents.stream()
                .collect(Collectors.groupingBy(JobEvent::getAction, LinkedHashMap::new, Collectors.toList()));

        List<ImportProgressStage> stages = new ArrayList<>(eventsByAction.size());
        for (Map.Entry<String, List<JobEvent>> actionEvents : eventsByAction.entrySet()) {
            toStage(actionEvents.getKey(), actionEvents.getValue(), correlationId).ifPresent(stages::add);
        }

        return new ImportProgress()
                .codespace(codespace)
                .correlationId(correlationId)
                .stages(stages);
    }

    /**
     * @param eventsForAction the surviving events for one action, ordered by event time
     * @return empty if the action or its state has no external name, so the enums stay closed
     */
    private static Optional<ImportProgressStage> toStage(String action, List<JobEvent> eventsForAction, String correlationId) {
        ImportProgressStage.StageEnum stage = STAGE_BY_ACTION.get(action);
        if (stage == null) {
            // Deployment skew: marduk emits a step nabu has not been taught about. Monitoring this
            // log line is how a real pipeline step missing from the response gets noticed.
            logger.warn("No external stage name for timetable action '{}' (correlation id '{}'); omitting it from the import progress response.", action, correlationId);
            return Optional.empty();
        }

        JobEvent latest = eventsForAction.getLast();
        ImportProgressStage.StatusEnum status = STATUS_BY_STATE.get(latest.getState());
        if (status == null) {
            logger.info("Job state '{}' on timetable action '{}' (correlation id '{}') has no external status; omitting the stage from the import progress response.", latest.getState(), action, correlationId);
            return Optional.empty();
        }

        Instant startedAt = eventsForAction.stream()
                .filter(event -> JobState.STARTED.equals(event.getState()))
                .map(JobEvent::getEventTime)
                .min(Comparator.naturalOrder())
                .orElse(null);

        Instant completedAt = JobEventAggregation.isTerminal(latest.getState()) ? latest.getEventTime() : null;

        // marduk reads whatever JOB_ERROR_CODE sits on the exchange, so a code can be inherited from
        // an earlier step. Keyed on JobState.FAILED, not the external FAILED that TIMEOUT also maps
        // to: a timed-out stage gives no reason of its own, so any code on it was inherited.
        String errorCode = JobState.FAILED.equals(latest.getState()) ? latest.getErrorCode() : null;

        return Optional.of(new ImportProgressStage()
                .stage(stage)
                .status(status)
                .startedAt(atDefaultOffset(startedAt))
                .completedAt(atDefaultOffset(completedAt))
                .errorCode(errorCode));
    }
}
