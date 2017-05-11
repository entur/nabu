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
    private Long id;

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
}
