package no.rutebanken.nabu.rest.external;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Path("/openapi.json")
public class ExternalOpenApiResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFile() {
        InputStream in = getClass().getResourceAsStream("/openapi/openapi-events-external.json");
        return Response.ok(in).build();
    }
}