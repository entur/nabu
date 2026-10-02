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

package no.rutebanken.nabu.event.aggregation;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobEventAggregationTest {

    private static final Instant T0 = Instant.parse("2026-09-30T10:30:00Z");
    private static final String CORR_ID = "corr-id";

    private static JobEvent event(String action, JobState state, long secondsAfterT0) {
        return new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file.zip", 2L, null, action,
                state, CORR_ID, T0.plusSeconds(secondsAfterT0), "ost");
    }

    @Test
    void okAndAllErrorStatesAreTerminal() {
        assertTrue(JobEventAggregation.isTerminal(JobState.OK));
        assertTrue(JobEventAggregation.isTerminal(JobState.FAILED));
        assertTrue(JobEventAggregation.isTerminal(JobState.TIMEOUT));
        assertTrue(JobEventAggregation.isTerminal(JobState.CANCELLED));
        assertTrue(JobEventAggregation.isTerminal(JobState.DUPLICATE));
        assertFalse(JobEventAggregation.isTerminal(JobState.PENDING));
        assertFalse(JobEventAggregation.isTerminal(JobState.STARTED));
    }

    @Test
    void outputIsSortedByEventTime() {
        List<JobEvent> kept = JobEventAggregation.withoutStaleNonTerminalEvents(List.of(
                event("LINKING", JobState.OK, 3),
                event("LINKING", JobState.PENDING, 1),
                event("LINKING", JobState.STARTED, 2)));

        assertEquals(List.of(JobState.PENDING, JobState.STARTED, JobState.OK),
                kept.stream().map(JobEvent::getState).toList());
    }

    @Test
    void nonTerminalEventAfterTerminalOneForTheSameActionIsDropped() {
        // Event times are inverted: OK at +2 precedes STARTED at +3.
        List<JobEvent> kept = JobEventAggregation.withoutStaleNonTerminalEvents(List.of(
                event("LINKING", JobState.PENDING, 1),
                event("LINKING", JobState.OK, 2),
                event("LINKING", JobState.STARTED, 3)));

        assertEquals(2, kept.size());
        assertEquals(JobState.OK, kept.getLast().getState());
    }

    @Test
    void terminalStateOfOneActionDoesNotSuppressAnother() {
        List<JobEvent> kept = JobEventAggregation.withoutStaleNonTerminalEvents(List.of(
                event("LINKING", JobState.OK, 1),
                event("FILTERING", JobState.STARTED, 2)));

        assertEquals(2, kept.size());
        assertEquals("FILTERING", kept.getLast().getAction());
        assertEquals(JobState.STARTED, kept.getLast().getState());
    }

    /**
     * Events that were never persisted have no primary key to break a tie on, so a stable sort
     * leaves them in the order they arrived.
     */
    @Test
    void eventsWithEqualTimesAndNoKeyKeepTheOrderTheyWereGivenIn() {
        List<JobEvent> kept = JobEventAggregation.withoutStaleNonTerminalEvents(List.of(
                event("LINKING", JobState.STARTED, 1),
                event("FILTERING", JobState.STARTED, 1)));

        assertEquals(List.of("LINKING", "FILTERING"), kept.stream().map(JobEvent::getAction).toList());
    }

    /**
     * Event times are truncated to microseconds, so two events of one action can share one. The
     * primary key settles which of them is the action's last event, so the outcome does not depend
     * on the order the database returned the rows in.
     */
    @Test
    void eventsWithEqualTimesAreOrderedByPrimaryKey() {
        JobEvent ok = event("LINKING", JobState.OK, 1);
        ok.setPk(1L);
        JobEvent staleStarted = event("LINKING", JobState.STARTED, 1);
        staleStarted.setPk(2L);

        assertEquals(List.of(JobState.OK),
                JobEventAggregation.withoutStaleNonTerminalEvents(List.of(ok, staleStarted))
                        .stream().map(JobEvent::getState).toList());
        assertEquals(List.of(JobState.OK),
                JobEventAggregation.withoutStaleNonTerminalEvents(List.of(staleStarted, ok))
                        .stream().map(JobEvent::getState).toList());
    }
}
