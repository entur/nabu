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
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.rest.openapi.model.ImportProgress;
import no.rutebanken.nabu.rest.openapi.model.ImportProgressStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportProgressMapperTest {

    private static final Instant T0 = Instant.parse("2026-09-30T10:30:00Z");
    private static final String CODESPACE = "RUT";
    private static final String CORR_ID = "9f3c1b2e-7a44-4f10-9c1d-2b8e5a6f0d13";

    private static JobEvent event(String action, JobState state, long secondsAfterT0) {
        return new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file.zip", 2L, null, action,
                state, CORR_ID, T0.plusSeconds(secondsAfterT0), "ost");
    }

    private static ImportProgressStage onlyStage(List<JobEvent> events) {
        List<ImportProgressStage> stages = ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID, events).getStages();
        assertEquals(1, stages.size(), "expected exactly one stage, got " + stages);
        return stages.getFirst();
    }

    // ---- Stage vocabulary ----------------------------------------------------------------------

    /**
     * Every documented action, spelled as the literal string marduk writes into Event.action.
     * Deliberately not driven off TimeTableAction: the mapping must survive that enum drifting from
     * marduk's.
     */
    @ParameterizedTest
    @CsvSource({
            "FILE_TRANSFER,                       FILE_TRANSFER",
            "FILE_CLASSIFICATION,                 FILE_CLASSIFICATION",
            "PREVALIDATION,                       PREVALIDATION",
            "IMPORT,                              IMPORT",
            "VALIDATION_LEVEL_1,                  VALIDATION_LEVEL_1",
            "VALIDATION_LEVEL_2,                  VALIDATION_LEVEL_2",
            "DATASPACE_TRANSFER,                  DATASPACE_TRANSFER",
            "LINKING,                             LINKING",
            "FILTERING,                           FILTERING",
            "EXPORT_NETEX,                        NETEX_EXPORT",
            "EXPORT_NETEX_POSTVALIDATION,         POSTVALIDATION",
            "EXPORT_NETEX_BLOCKS,                 BLOCKS_EXPORT",
            "EXPORT_NETEX_BLOCKS_POSTVALIDATION,  BLOCKS_POSTVALIDATION",
            "EXPORT_NETEX_MERGED_POSTVALIDATION,  MERGED_POSTVALIDATION",
            "EXPORT,                              GTFS_EXPORT",
            "OTP2_BUILD_GRAPH,                    GRAPH_BUILD"
    })
    void mapsEveryDocumentedActionToItsExternalStage(String action, String expectedStage) {
        assertEquals(ImportProgressStage.StageEnum.valueOf(expectedStage),
                onlyStage(List.of(event(action, JobState.OK, 1))).getStage());
    }

    /**
     * The other direction: every action nabu's own enum knows about, except CLEAN which marduk
     * declares but never emits, must be mapped. A value added to the enum without a mapping would
     * otherwise silently disappear from the response.
     */
    @Test
    void everyKnownTimeTableActionExceptCleanIsMapped() {
        List<String> unmapped = Arrays.stream(TimeTableAction.values())
                .map(Enum::name)
                .filter(action -> !TimeTableAction.CLEAN.name().equals(action))
                .filter(action -> ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID, List.of(event(action, JobState.OK, 1))).getStages().isEmpty())
                .toList();

        assertTrue(unmapped.isEmpty(), "unmapped TimeTableAction values: " + unmapped);
    }

    @Test
    void cleanIsNotMapped() {
        assertTrue(ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID,
                List.of(event("CLEAN", JobState.OK, 1))).getStages().isEmpty());
    }

    /**
     * Deployment skew: marduk gains a step before nabu learns about it. The unknown action is
     * dropped so the external enum stays closed, and the rest of the response still comes back.
     */
    @Test
    void unknownActionIsOmittedAndDoesNotFailTheRequest() {
        List<ImportProgressStage> stages = ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID, List.of(
                event("FILE_TRANSFER", JobState.OK, 1),
                event("SOME_ACTION_NABU_HAS_NEVER_HEARD_OF", JobState.OK, 2))).getStages();

        assertEquals(List.of(ImportProgressStage.StageEnum.FILE_TRANSFER),
                stages.stream().map(ImportProgressStage::getStage).toList());
    }

    // ---- Status vocabulary ---------------------------------------------------------------------

    /**
     * TIMEOUT is folded into FAILED: a stage that never came back is a failure, and the distinction
     * is not one an external consumer can act on.
     */
    @ParameterizedTest
    @CsvSource({
            "PENDING, PENDING",
            "STARTED, IN_PROGRESS",
            "OK,      COMPLETED",
            "FAILED,  FAILED",
            "TIMEOUT, FAILED"
    })
    void mapsEveryExposedJobStateToItsExternalStatus(String state, String expectedStatus) {
        assertEquals(ImportProgressStage.StatusEnum.valueOf(expectedStatus),
                onlyStage(List.of(event("LINKING", JobState.valueOf(state), 1))).getStatus());
    }

    /**
     * CANCELLED is not exposed. It conflates "deliberately not performed for this provider" with
     * "abandoned by a fault" and carries no error code either way, so there is nothing a partner
     * could do with it; the stage is dropped rather than given a status that misleads.
     */
    @Test
    void aCancelledStageIsOmitted() {
        List<ImportProgressStage> stages = ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID, List.of(
                event("FILE_TRANSFER", JobState.OK, 1),
                event("IMPORT", JobState.PENDING, 2),
                event("IMPORT", JobState.CANCELLED, 3))).getStages();

        assertEquals(List.of(ImportProgressStage.StageEnum.FILE_TRANSFER),
                stages.stream().map(ImportProgressStage::getStage).toList());
    }

    /**
     * DUPLICATE is unreachable for timetables — a duplicate upload is recorded as FILE_TRANSFER /
     * FAILED with errorCode ERROR_FILE_DUPLICATE — so it has no external name and is dropped rather
     * than passed through.
     */
    @Test
    void aDuplicateStateIsOmitted() {
        assertTrue(ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID,
                List.of(event("FILE_TRANSFER", JobState.DUPLICATE, 1))).getStages().isEmpty());
    }

    // ---- Response contract ---------------------------------------------------------------------

    @Test
    void echoesTheCodespaceAndCorrelationId() {
        ImportProgress progress = ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID,
                List.of(event("FILE_TRANSFER", JobState.OK, 1)));

        assertEquals(CODESPACE, progress.getCodespace());
        assertEquals(CORR_ID, progress.getCorrelationId());
    }

    @Test
    void reportsTheStartAndCompletionOfAFinishedStage() {
        ImportProgressStage stage = onlyStage(List.of(
                event("FILE_TRANSFER", JobState.PENDING, 0),
                event("FILE_TRANSFER", JobState.STARTED, 1),
                event("FILE_TRANSFER", JobState.OK, 4)));

        assertEquals(ImportProgressStage.StatusEnum.COMPLETED, stage.getStatus());
        assertEquals(OffsetDateTime.parse("2026-09-30T10:30:01Z"), stage.getStartedAt());
        assertEquals(OffsetDateTime.parse("2026-09-30T10:30:04Z"), stage.getCompletedAt());
    }

    @Test
    void leavesCompletedAtNullWhileAStageIsUnfinished() {
        ImportProgressStage stage = onlyStage(List.of(
                event("LINKING", JobState.STARTED, 1)));

        assertEquals(ImportProgressStage.StatusEnum.IN_PROGRESS, stage.getStatus());
        assertEquals(OffsetDateTime.parse("2026-09-30T10:30:01Z"), stage.getStartedAt());
        assertNull(stage.getCompletedAt());
    }

    /**
     * Some steps publish only their terminal event. Reporting no start for a stage that has
     * demonstrably run would be misleading, so it falls back to the time it finished.
     */
    @Test
    void aStageThatOnlyReportedItsTerminalEventStartsWhenItFinished() {
        ImportProgressStage stage = onlyStage(List.of(
                event("FILE_TRANSFER", JobState.OK, 4)));

        assertEquals(ImportProgressStage.StatusEnum.COMPLETED, stage.getStatus());
        assertEquals(OffsetDateTime.parse("2026-09-30T10:30:04Z"), stage.getStartedAt());
        assertEquals(OffsetDateTime.parse("2026-09-30T10:30:04Z"), stage.getCompletedAt());
    }

    @Test
    void fallsBackToTheCompletionTimeWhenTheTerminalEventOvertookTheStartEvent() {
        // The OK at +1 precedes the STARTED at +2, so the STARTED is dropped as stale and the stage
        // has no start of its own left to report.
        ImportProgressStage stage = onlyStage(List.of(
                event("LINKING", JobState.OK, 1),
                event("LINKING", JobState.STARTED, 2)));

        assertEquals(ImportProgressStage.StatusEnum.COMPLETED, stage.getStatus());
        assertEquals(OffsetDateTime.parse("2026-09-30T10:30:01Z"), stage.getStartedAt());
        assertEquals(OffsetDateTime.parse("2026-09-30T10:30:01Z"), stage.getCompletedAt());
    }

    /**
     * The flip side of the fallback: startedAt is null only while a stage is still queued.
     */
    @Test
    void startedAtIsNullOnlyWhileAStageIsPending() {
        assertNull(onlyStage(List.of(event("LINKING", JobState.PENDING, 1))).getStartedAt());
    }

    @Test
    void aStageWithOnlyAPendingEventHasNoTimestamps() {
        ImportProgressStage stage = onlyStage(List.of(
                event("OTP2_BUILD_GRAPH", JobState.PENDING, 1)));

        assertEquals(ImportProgressStage.StageEnum.GRAPH_BUILD, stage.getStage());
        assertEquals(ImportProgressStage.StatusEnum.PENDING, stage.getStatus());
        assertNull(stage.getStartedAt());
        assertNull(stage.getCompletedAt());
    }

    @Test
    void reportsOneElementPerStageWithTheLatestStateOnly() {
        List<ImportProgressStage> stages = ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID, List.of(
                event("FILE_TRANSFER", JobState.STARTED, 1),
                event("FILE_TRANSFER", JobState.OK, 2),
                event("PREVALIDATION", JobState.STARTED, 3),
                event("PREVALIDATION", JobState.OK, 4))).getStages();

        assertEquals(2, stages.size());
        assertEquals(ImportProgressStage.StatusEnum.COMPLETED, stages.getFirst().getStatus());
        assertEquals(ImportProgressStage.StatusEnum.COMPLETED, stages.getLast().getStatus());
    }

    @Test
    void ordersStagesByTheirEarliestEvent() {
        // Supplied in an order unrelated to time; the graph build starts last but finishes first.
        List<ImportProgressStage> stages = ImportProgressMapper.toImportProgress(CODESPACE, CORR_ID, List.of(
                event("OTP2_BUILD_GRAPH", JobState.STARTED, 30),
                event("OTP2_BUILD_GRAPH", JobState.OK, 31),
                event("PREVALIDATION", JobState.STARTED, 20),
                event("FILE_TRANSFER", JobState.STARTED, 10))).getStages();

        assertEquals(List.of(
                        ImportProgressStage.StageEnum.FILE_TRANSFER,
                        ImportProgressStage.StageEnum.PREVALIDATION,
                        ImportProgressStage.StageEnum.GRAPH_BUILD),
                stages.stream().map(ImportProgressStage::getStage).toList());
    }

    // ---- errorCode -----------------------------------------------------------------------------

    @Test
    void reportsTheErrorCodeOfAFailedStage() {
        JobEvent failed = event("FILTERING", JobState.FAILED, 2);
        failed.setErrorCode("NO_JOURNEYS_IN_NETEX_DATASET");

        ImportProgressStage stage = onlyStage(List.of(event("FILTERING", JobState.STARTED, 1), failed));

        assertEquals(ImportProgressStage.StatusEnum.FAILED, stage.getStatus());
        assertEquals("NO_JOURNEYS_IN_NETEX_DATASET", stage.getErrorCode());
    }

    /**
     * Prevalidation reports no error code. What was wrong with the data is answered by the
     * validation report, which partners fetch separately under the same correlation id, so FAILED
     * with a null errorCode is a complete answer rather than a missing one.
     */
    @Test
    void reportsAFailedStageThatCarriesNoErrorCode() {
        ImportProgressStage stage = onlyStage(List.of(
                event("PREVALIDATION", JobState.STARTED, 1),
                event("PREVALIDATION", JobState.FAILED, 2)));

        assertEquals(ImportProgressStage.StatusEnum.FAILED, stage.getStatus());
        assertNull(stage.getErrorCode());
    }

    /**
     * marduk reads whatever JOB_ERROR_CODE happens to sit on the exchange, so a code can be
     * inherited from an earlier step and land on an event that did not fail. A code next to a
     * COMPLETED stage is at best meaningless, so it is suppressed.
     */
    @Test
    void suppressesAnInheritedErrorCodeOnAStageThatSucceeded() {
        JobEvent succeeded = event("LINKING", JobState.OK, 2);
        succeeded.setErrorCode("ERROR_FILE_INVALID_XML_CONTENT");

        ImportProgressStage stage = onlyStage(List.of(event("LINKING", JobState.STARTED, 1), succeeded));

        assertEquals(ImportProgressStage.StatusEnum.COMPLETED, stage.getStatus());
        assertNull(stage.getErrorCode());
    }

    /**
     * A timeout reports FAILED but carries no reason of its own, so any code on the event was
     * inherited from an earlier step and must not be presented as the cause.
     */
    @Test
    void suppressesAnInheritedErrorCodeOnATimedOutStage() {
        JobEvent timedOut = event("IMPORT", JobState.TIMEOUT, 2);
        timedOut.setErrorCode("ERROR_FILE_INVALID_XML_CONTENT");

        ImportProgressStage stage = onlyStage(List.of(timedOut));

        assertEquals(ImportProgressStage.StatusEnum.FAILED, stage.getStatus());
        assertNull(stage.getErrorCode());
    }
}
