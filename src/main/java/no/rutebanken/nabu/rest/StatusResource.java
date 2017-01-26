package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.repository.StatusRepository;
import no.rutebanken.nabu.rest.domain.JobStatus;
import no.rutebanken.nabu.rest.domain.JobStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
@Produces("application/json")
@Path("/jobs")
public class StatusResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatusRepository statusRepository;

    @GET
    @Path("/{providerId}")
    public List<JobStatus> listStatus(@PathParam("providerId") Long providerId) {
        logger.info("Returning status for provider with id '" + providerId + "'");
        try {
			List<Status> statusForProvider = statusRepository.getStatusForProvider(providerId);
			return convert(statusForProvider);
		} catch (Exception e) {
			logger.error("Erring fetching status for provider with id "+providerId+": "+e.getMessage(),e);
			throw e;
		}
    }

	public List<JobStatus> convert(List<Status> statusForProvider) {
        List<JobStatus>  list = new ArrayList<>();
        // Map from internal Status object to Rest service JobStatusEvent object
        String correlationId = null;
        JobStatus currentAggregation = null;
        for(Status in : statusForProvider) {

        	if(!in.correlationId.equals(correlationId)) {
        		
        		correlationId = in.correlationId;

        		// Create new Aggregation
        		currentAggregation = new JobStatus();
        		currentAggregation.setFirstEvent(in.date);
        		currentAggregation.setFileName(in.fileName);
        		currentAggregation.setCorrelationId(in.correlationId);
				currentAggregation.setChouetteJobId(in.jobId);

        		list.add(currentAggregation);
        	}

			if(in.jobId != null){
				currentAggregation.setChouetteJobId(in.jobId);
			}

        	currentAggregation.addEvent(JobStatusEvent.createFromStatus(in));
        }

        for(JobStatus agg : list) {
        	JobStatusEvent event = agg.getEvents().get(agg.getEvents().size()-1);
			agg.setLastEvent(event.date);
			agg.setEndStatus(event.state);
			long durationMillis = agg.getLastEvent().getTime() - agg.getFirstEvent().getTime();
			agg.setDurationMillis(durationMillis);
        }

        Collections.sort(list, (o1, o2) -> o1.getFirstEvent().compareTo(o2.getFirstEvent()));

        return list;
	}

}
