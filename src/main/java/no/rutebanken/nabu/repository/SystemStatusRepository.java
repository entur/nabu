package no.rutebanken.nabu.repository;


import no.rutebanken.nabu.domain.SystemStatus;

import java.util.Date;
import java.util.List;

public interface SystemStatusRepository {

	void add(SystemStatus systemStatus);

	List<SystemStatus> getSystemStatus(Date from, Date to, List<String> jobTypes, List<SystemStatus.Action> actions,
			                                  List<SystemStatus.State> states, List<String> entities,
			                                  List<String> sources, List<String> targets);

	List<SystemStatus> getLatestSystemStatus(List<String> jobTypes, List<SystemStatus.Action> actions,
			                                        List<SystemStatus.State> states, List<String> entities,
			                                        List<String> sources, List<String> targets);
}
