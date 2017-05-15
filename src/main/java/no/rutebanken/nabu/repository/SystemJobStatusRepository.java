package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

public interface SystemJobStatusRepository extends JpaRepository<SystemJobStatus, Long> {


    List<SystemJobStatus> find(List<String> jobDomains, List<String> actions);

    SystemJobStatus findByJobDomainAndActionAndState(String jobDomain, String action, JobState state);
}
