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

package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;

import java.time.ZonedDateTime;
import java.util.Collection;

import static no.rutebanken.nabu.domain.event.JobState.ERROR_JOB_STATES;

public class DataDeliveryStatus {

    public enum State {
        IN_PROGRESS, FAILED, OK;

        /**
         * Return the state of the delivery given a set of statuses.
         * Some steps in the import pipeline can run in parallel, thus the chronological order is arbitrary.
         * In particular, the OTP build graph event is not necessarily the last event recorded in the pipeline.
         */
        public static DataDeliveryStatus.State of(Collection<JobEvent> statuses) {
            if (statuses.stream().anyMatch(e -> TimeTableAction.OTP2_BUILD_GRAPH.toString().equals(e.getAction()) && JobState.OK.equals(e.getState()))) {
                return DataDeliveryStatus.State.OK;
            } else if (statuses.stream().anyMatch(e -> ERROR_JOB_STATES.contains(e.getState()))) {
                return DataDeliveryStatus.State.FAILED;
            } else {
                return DataDeliveryStatus.State.IN_PROGRESS;
            }
        }

    }

    @JsonProperty("state")
    public DataDeliveryStatus.State state;

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public ZonedDateTime date;

    @JsonProperty("fileName")
    public String fileName;

    public DataDeliveryStatus(DataDeliveryStatus.State state, ZonedDateTime date, String fileName) {
        this.state = state;
        this.date = date;
        this.fileName = fileName;
    }

    public DataDeliveryStatus() {
    }


}
