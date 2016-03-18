package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.repository.StatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;


@Component
@Produces("application/json")
@Path("/opstatus")
public class StatusResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatusRepository statusRepository;

    @GET
    @Path("/{providerId}/status")
    public Collection<Status> listStatus(@PathParam("providerId") Long providerId) {
        logger.info("Returning status for provider with id '" + providerId + "'");
        return statusRepository.getStatusForProvider(providerId);
    }

}
