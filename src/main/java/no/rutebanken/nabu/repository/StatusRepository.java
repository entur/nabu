package no.rutebanken.nabu.repository;

import java.util.Date;
import java.util.List;

import no.rutebanken.nabu.domain.Status;

public interface StatusRepository {

	void add(Status status);

	List<Status> getStatusForProvider(Long providerId, Date from, Date to, List<Status.Action> actions,
			                                 List<Status.State> states, List<Long> jobIds, List<String> fileNames);


	List<Status> getLatestDeliveryStatusForProvider(Long providerId);

	void clearAll();

	void clear(Long providerId);

}
