package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.SystemJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemJobStatusRepository extends JpaRepository<SystemJobStatus, Long> {


    List<SystemJobStatus> find(List<String> jobDomains,List<String> jobTypes);

}
