package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import no.rutebanken.nabu.rest.domain.SystemStatusAggregation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Produces("application/json")
@Path("/systemJobs")
public class SystemJobResource {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;


    @GET
    @Path("/status/aggregation")
    public Collection<SystemStatusAggregation> getLatestSystemStatus(@QueryParam("jobType") List<String> jobTypes,
                                                                            @QueryParam("action") List<String> actions,
                                                                            @QueryParam("state") List<JobState> states, @QueryParam("entity") List<String> entities,
                                                                            @QueryParam("source") List<String> sources, @QueryParam("target") List<String> targets
    ) {
        logger.debug("Returning aggregated system status");
        try {
            return convertToSystemStatusAggregation( systemJobStatusRepository.findAll());
        } catch (Exception e) {
            logger.error("Erring fetching system status: " + e.getMessage(), e);
            throw e;
        }
    }

    Collection<SystemStatusAggregation> convertToSystemStatusAggregation(List<SystemJobStatus> systemStatuses) {
        Map<Pair<String,String>, SystemStatusAggregation> aggregationPerJobType = new HashMap<>();

        for (SystemJobStatus in : systemStatuses) {

            Pair<String,String> key=Pair.of(in.getJobDomain(),in.getJobType());
            SystemStatusAggregation currentAggregation = aggregationPerJobType.get(key);

            if (currentAggregation == null) {
                currentAggregation = new SystemStatusAggregation(in);
                aggregationPerJobType.put(key, currentAggregation);
            }
            currentAggregation.addSystemStatus(in);
        }


        return aggregationPerJobType.values();
    }

}
