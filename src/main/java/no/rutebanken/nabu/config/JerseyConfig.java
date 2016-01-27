package no.rutebanken.nabu.config;

import no.rutebanken.nabu.rest.FileUploadResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import javax.servlet.Filter;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {

//        packages("org.glassfish.jersey.jaxb;com.fasterxml.jackson.jaxrs.xml");
//        register(StopPlaceResource.class);
//        register(QuayResource.class);
        register(FileUploadResource.class);
        register(MultiPartFeature.class);
    }

}
