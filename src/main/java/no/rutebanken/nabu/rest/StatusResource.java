package no.rutebanken.nabu.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.repository.StatusRepository;
import no.rutebanken.nabu.rest.domain.JobStatus;
import no.rutebanken.nabu.rest.domain.JobStatusEvent;


@Component
@Produces("application/json")
public class StatusResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatusRepository statusRepository;

    @GET
    @Path("/{providerId}/jobs")
    public List<JobStatus> listStatus(@PathParam("providerId") Long providerId) {
        logger.info("Returning status for provider with id '" + providerId + "'");
        List<Status> statusForProvider = statusRepository.getStatusForProvider(providerId);
        
        return convert(statusForProvider);
        
    }

	public List<JobStatus> convert(List<Status> statusForProvider) {
        List<JobStatus>  list = new ArrayList<JobStatus>();        
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
        		
        		list.add(currentAggregation);
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

        
        
        // Sort
        Collections.sort(list, new Comparator<JobStatus>() {

			@Override
			public int compare(JobStatus o1, JobStatus o2) {
				return o1.getFirstEvent().compareTo(o2.getFirstEvent());
			}});
        
        return list;
	}

}
