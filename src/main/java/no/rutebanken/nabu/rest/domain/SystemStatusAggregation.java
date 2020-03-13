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
import com.fasterxml.jackson.annotation.JsonRootName;
import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobState;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

@JsonRootName("systemStatusAggregation")
public class SystemStatusAggregation {

    @JsonProperty("jobDomain")
    public String jobDomain;

    @JsonProperty("jobType")
    public String jobType;

    @JsonProperty("currentState")
    public JobState currentState;

    @JsonProperty("currentStateDate")
    public Date currentStateDate;

    @JsonProperty("latestDatePerState")
    public Map<JobState, Date> latestDatePerState = new EnumMap<>(JobState.class);


    public SystemStatusAggregation(SystemJobStatus in) {
        this.jobType = in.getAction();
        this.jobDomain = in.getJobDomain();
    }


    public void addSystemStatus(SystemJobStatus in) {

        Date statusDate=Date.from(in.getLastStatusTime());
        if (currentStateDate == null || currentStateDate.before(statusDate)) {
            currentStateDate = statusDate;
            currentState = in.getState();
        }

        Date latestDateForState = latestDatePerState.get(in.getState());
        if (latestDateForState == null || latestDateForState.before(statusDate)) {
            latestDatePerState.put(in.getState(), statusDate);
        }

    }
}
