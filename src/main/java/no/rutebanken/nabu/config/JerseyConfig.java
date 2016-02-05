package no.rutebanken.nabu.config;

import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.rest.FileUploadResource;
import no.rutebanken.nabu.rest.StatusResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MultiPartFeature.class);
        register(FileUploadResource.class);
        register(StatusResource.class);
        register(CorsResponseFilter.class);
    }

}
