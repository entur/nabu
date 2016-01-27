package no.rutebanken.nabu.config;

import no.rutebanken.nabu.rest.FileUploadResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {

//        packages("org.glassfish.jersey.jaxb;com.fasterxml.jackson.jaxrs.xml");

        register(MultiPartFeature.class);
        register(FileUploadResource.class);
    }

}
