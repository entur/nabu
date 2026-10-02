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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.rutebanken.nabu.domain.event.JobState.ERROR_JOB_STATES;

/**
 * Rules shared by everything that rolls a correlation's job events up into a status.
 */
public final class JobEventAggregation {

    /**
     * Chronological order of job events.
     * <p>
     * Event times are truncated to microseconds, so two events of one action can share one. The
     * primary key breaks the tie, which orders them by insertion. Unsaved events have no key and
     * compare equal, leaving them in whatever order a stable sort was given them in.
     */
    public static final Comparator<JobEvent> BY_EVENT_TIME =
            Comparator.comparing(JobEvent::getEventTime)
                    .thenComparing(JobEvent::getPk, Comparator.nullsLast(Comparator.naturalOrder()));

    private JobEventAggregation() {
    }

    /**
     * Whether a state is one an action does not come back from.
     */
    public static boolean isTerminal(JobState state) {
        return JobState.OK.equals(state) || ERROR_JOB_STATES.contains(state);
    }

    /**
     * Sort the events of a single correlation by event time and drop the stale ones.
     * <p>
     * A PENDING or STARTED event that sorts after a terminal event for the same action is stale:
     * Pub/Sub delivers a service's STARTED and result messages out of order often enough that the
     * relayed event times end up inverted, and without this guard the stale STARTED becomes the
     * action's last event and a finished stage reports as still running.
     * <p>
     * Ordering is {@link #BY_EVENT_TIME}, so which of two events sharing an event time counts as
     * the action's last does not depend on the order the caller supplied them in.
     *
     * @param eventsOfOneCorrelation events sharing a single correlation id, in any order
     * @return the surviving events, ordered by event time
     */
    public static List<JobEvent> withoutStaleNonTerminalEvents(List<JobEvent> eventsOfOneCorrelation) {
        List<JobEvent> sorted = eventsOfOneCorrelation.stream()
                .sorted(BY_EVENT_TIME)
                .toList();

        Set<String> terminalActions = new HashSet<>();
        List<JobEvent> kept = new ArrayList<>(sorted.size());
        for (JobEvent event : sorted) {
            if (isTerminal(event.getState())) {
                terminalActions.add(event.getAction());
            } else if (terminalActions.contains(event.getAction())) {
                continue;
            }
            kept.add(event);
        }
        return kept;
    }
}
