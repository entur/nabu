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

package no.rutebanken.nabu.domain;

import no.rutebanken.nabu.domain.event.JobState;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
                                   @UniqueConstraint(name = "system_job_status_unique", columnNames = {"jobDomain", "action", "state"})
})
public class SystemJobStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    private String jobDomain;

    private String action;

    private JobState state;

    private Instant lastStatusTime;

    public SystemJobStatus(String jobDomain, String action, JobState state, Instant lastStatusTime) {
        this.jobDomain = jobDomain;
        this.action = action;
        this.state = state;
        this.lastStatusTime = lastStatusTime;
    }

    public SystemJobStatus() {

    }

    public String getJobDomain() {
        return jobDomain;
    }

    public void setJobDomain(String jobDomain) {
        this.jobDomain = jobDomain;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public Instant getLastStatusTime() {
        return lastStatusTime;
    }

    public void setLastStatusTime(Instant lastStatusTime) {
        this.lastStatusTime = lastStatusTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemJobStatus that = (SystemJobStatus) o;

        if (!Objects.equals(jobDomain, that.jobDomain)) return false;
        if (!Objects.equals(action, that.action)) return false;
        if (state != that.state) return false;
        return Objects.equals(lastStatusTime, that.lastStatusTime);
    }

    @Override
    public int hashCode() {
        int result = jobDomain != null ? jobDomain.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (lastStatusTime != null ? lastStatusTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SystemJobStatus{" +
                       "jobDomain='" + jobDomain + '\'' +
                       ", action='" + action + '\'' +
                       ", state=" + state +
                       ", lastStatusTime=" + lastStatusTime +
                       '}';
    }
}
