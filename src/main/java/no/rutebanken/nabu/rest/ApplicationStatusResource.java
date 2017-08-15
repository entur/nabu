package no.rutebanken.nabu.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.rutebanken.nabu.repository.DbStatus;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Component
@Produces("application/json")
@Path("/appstatus")
@Api(tags = {"Application status resource"}, produces = "text/plain")
public class ApplicationStatusResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    EventRepository eventRepository;

    @GET
    @Path("/ready")
    @ApiOperation(value = "Checks application readiness, including db connection", response = Void.class)
    @ApiResponses(value = {
                                  @ApiResponse(code = 200, message = "application is ready"),
                                  @ApiResponse(code = 500, message = "application is not ready")
    })
    public Response isReady() {
        logger.debug("Checking readiness...");
        if (((DbStatus) providerRepository).isDbUp() && ((DbStatus) eventRepository).isDbUp()) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/up")
    @ApiOperation(value = "Returns OK if application is running", response = Void.class)
    @ApiResponses(value = {
                                  @ApiResponse(code = 200, message = "application is running")
    })
    public Response isUp() {
        return Response.ok().build();
    }

}
