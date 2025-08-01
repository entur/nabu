/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.rest.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import no.rutebanken.nabu.rest.domain.SystemStatusAggregation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Produces("application/json")
@Path("admin_summary")
@Tags(value = {
        @Tag(name = "AdminSummaryResource", description ="Admin summary resource")
})
@PreAuthorize("@authorizationService.isRouteDataAdmin()")
public class AdminSummaryResource {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SystemJobStatusRepository systemJobStatusRepository;

    public AdminSummaryResource(SystemJobStatusRepository systemJobStatusRepository) {
        this.systemJobStatusRepository = systemJobStatusRepository;
    }


    @GET
    @Path("/status/aggregation")
    @Operation(summary = "Return the status of the latest dataset import")
    public Collection<SystemStatusAggregation> getLatestSystemStatus(@QueryParam("jobDomain") List<String> jobDomains,
                                                                            @QueryParam("jobType") List<String> jobTypes
    ) {
        logger.debug("Returning aggregated system status");
        try {
            return convertToSystemStatusAggregation(systemJobStatusRepository.find(jobDomains, jobTypes));
        } catch (Exception e) {
            logger.error("Erring fetching system status: {}", e.getMessage(), e);
            throw e;
        }
    }

    Collection<SystemStatusAggregation> convertToSystemStatusAggregation(List<SystemJobStatus> systemStatuses) {
        Map<Pair<String, String>, SystemStatusAggregation> aggregationPerJobType = new HashMap<>();

        for (SystemJobStatus in : systemStatuses) {
            Pair<String, String> key = Pair.of(in.getJobDomain(), in.getAction());
            SystemStatusAggregation currentAggregation = aggregationPerJobType.computeIfAbsent(key, k -> new SystemStatusAggregation(in));
            currentAggregation.addSystemStatus(in);
        }


        return aggregationPerJobType.values();
    }

}
