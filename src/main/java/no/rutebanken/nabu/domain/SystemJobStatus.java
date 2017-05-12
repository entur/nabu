package no.rutebanken.nabu.domain;

import no.rutebanken.nabu.domain.event.JobState;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(uniqueConstraints = {
                                   @UniqueConstraint(name = "system_job_status_unique", columnNames = {"jobDomain", "jobType", "state"})
})
public class SystemJobStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    private String jobDomain;

    private String jobType;

    private JobState state;

    private Instant lastStatusTime;

    public SystemJobStatus(String jobDomain, String jobType, JobState state, Instant lastStatusTime) {
        this.jobDomain = jobDomain;
        this.jobType = jobType;
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

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
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

        if (jobDomain != null ? !jobDomain.equals(that.jobDomain) : that.jobDomain != null) return false;
        if (jobType != null ? !jobType.equals(that.jobType) : that.jobType != null) return false;
        if (state != that.state) return false;
        return lastStatusTime != null ? lastStatusTime.equals(that.lastStatusTime) : that.lastStatusTime == null;
    }

    @Override
    public int hashCode() {
        int result = jobDomain != null ? jobDomain.hashCode() : 0;
        result = 31 * result + (jobType != null ? jobType.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (lastStatusTime != null ? lastStatusTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SystemJobStatus{" +
                       "jobDomain='" + jobDomain + '\'' +
                       ", jobType='" + jobType + '\'' +
                       ", state=" + state +
                       ", lastStatusTime=" + lastStatusTime +
                       '}';
    }
}
