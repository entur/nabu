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

import com.fasterxml.jackson.annotation.JsonProperty;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.rest.domain.JobStatus.State;

import java.util.Date;

public class JobStatusEvent {

    @JsonProperty("state")
    public State state;

    @JsonProperty("date")
    public Date date;

    @JsonProperty("action")
    public String action;

    @JsonProperty("chouetteJobId")
    public Long chouetteJobId;

    @JsonProperty("referential")
    public String referential;


    public JobStatusEvent(String action, State state, Date date, Long chouetteJobId, String referential) {
        this.action = action;
        this.state = state;
        this.date = date;
        this.chouetteJobId = chouetteJobId;
        this.referential = referential;
    }

    public static JobStatusEvent createFromJobEvent(JobEvent e) {
        Long chouetteId = e.getExternalId() == null ? null : Long.parseLong(e.getExternalId());
        return new JobStatusEvent(e.getAction(),
                                         JobStatus.State.valueOf(e.getState().name()), Date.from(e.getEventTime()), chouetteId
                                         , e.getReferential());
    }
}
