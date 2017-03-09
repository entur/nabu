package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.SystemStatus;
import no.rutebanken.nabu.repository.SystemStatusRepository;
import no.rutebanken.nabu.rest.domain.SystemJobStatus;
import no.rutebanken.nabu.rest.domain.SystemJobStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static no.rutebanken.nabu.rest.mapper.EnumMapper.convertEnums;

@Component
@Produces("application/json")
@Path("/systemJobs")
public class SystemJobResource {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SystemStatusRepository systemStatusRepository;

	@GET
	@Path("/status")
	public List<SystemJobStatus> listSystemStatus(@QueryParam("from") Date from,
			                                             @QueryParam("to") Date to, @QueryParam("action") List<SystemJobStatus.Action> actions,
			                                             @QueryParam("state") List<SystemJobStatus.State> states, @QueryParam("entity") List<String> entities,
			                                             @QueryParam("source") List<String> sources, @QueryParam("target") List<String> targets
	) {
		logger.info("Returning system status");
		try {
			List<SystemStatus> internalStatuses = systemStatusRepository.getSystemStatus(from, to,
					convertEnums(actions, SystemStatus.Action.class), convertEnums(states, SystemStatus.State.class), entities,sources,targets);
			return convert(internalStatuses);
		} catch (Exception e) {
			logger.error("Erring fetching system status: " + e.getMessage(), e);
			throw e;
		}
	}


	private List<SystemJobStatus> convert(List<SystemStatus> systemStatuses) {

		List<SystemJobStatus> list = new ArrayList<>();
		// Map from internal Status object to Rest service SystemJobStatus object
		String correlationId = null;
		SystemJobStatus currentAggregation = null;
		for (SystemStatus in : systemStatuses) {

			if (!in.correlationId.equals(correlationId)) {

				correlationId = in.correlationId;

				// Create new Aggregation
				currentAggregation = new SystemJobStatus();
				currentAggregation.setFirstEvent(in.date);
				currentAggregation.setEntity(in.entity);
				currentAggregation.setSource(in.source);
				currentAggregation.setTarget(in.target);
				currentAggregation.setAction(SystemJobStatus.Action.valueOf(in.action.name()));
				currentAggregation.setCorrelationId(in.correlationId);

				list.add(currentAggregation);
			}


			currentAggregation.addEvent(SystemJobStatusEvent.createFromSystemStatus(in));
		}

		for (SystemJobStatus agg : list) {
			SystemJobStatusEvent event = agg.getEvents().get(agg.getEvents().size() - 1);
			agg.setLastEvent(event.date);
			agg.setEndStatus(event.state);
			long durationMillis = agg.getLastEvent().getTime() - agg.getFirstEvent().getTime();
			agg.setDurationMillis(durationMillis);
		}

		Collections.sort(list, (o1, o2) -> o1.getFirstEvent().compareTo(o2.getFirstEvent()));

		return list;
	}
}
