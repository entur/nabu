package no.rutebanken.nabu.config;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.health.rest.HealthResource;
import no.rutebanken.nabu.rest.ChangeLogResource;
import no.rutebanken.nabu.rest.DataDeliveryStatusResource;
import no.rutebanken.nabu.rest.NotificationResource;
import no.rutebanken.nabu.rest.SystemJobResource;
import no.rutebanken.nabu.rest.TimeTableJobEventResource;
import no.rutebanken.nabu.rest.exception.AccessDeniedExceptionMapper;
import no.rutebanken.nabu.rest.exception.NotAuthenticatedExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig {


    @Bean
    public ServletRegistrationBean publicJersey() {
        ServletRegistrationBean publicJersey
                = new ServletRegistrationBean(new ServletContainer(new ServicesConfig()));
        publicJersey.addUrlMappings("/services/*");
        publicJersey.setName("PublicJersey");
        publicJersey.setLoadOnStartup(0);
        return publicJersey;
    }

    @Bean
    public ServletRegistrationBean privateJersey() {
        ServletRegistrationBean privateJersey
                = new ServletRegistrationBean(new ServletContainer(new HealthConfig()));
        privateJersey.addUrlMappings("/health/*");
        privateJersey.setName("PrivateJersey");
        privateJersey.setLoadOnStartup(1);
        return privateJersey;
    }


    private class ServicesConfig extends ResourceConfig {

        public ServicesConfig() {
            register(CorsResponseFilter.class);

            register(TimeTableJobEventResource.class);
            register(SystemJobResource.class);
            register(DataDeliveryStatusResource.class);
            register(NotificationResource.class);
            register(ChangeLogResource.class);

            register(NotAuthenticatedExceptionMapper.class);
            register(AccessDeniedExceptionMapper.class);

            configureSwagger();
        }


        private void configureSwagger() {
            // Available at localhost:port/api/swagger.json
            this.register(ApiListingResource.class);
            this.register(SwaggerSerializers.class);

            BeanConfig config = new BeanConfig();
            config.setConfigId("events-swagger-doc");
            config.setTitle("Event API");
            config.setVersion("v1");
            config.setSchemes(new String[]{"http", "https"});
            config.setBasePath("/services");
            config.setResourcePackage("no.rutebanken.nabu.rest");
            config.setPrettyPrint(true);
            config.setScan(true);
        }
    }


    private class HealthConfig extends ResourceConfig {

        public HealthConfig() {
            register(HealthResource.class);
            configureSwagger();
        }


        private void configureSwagger() {
            // Available at localhost:port/api/swagger.json
            this.register(ApiListingResource.class);
            this.register(SwaggerSerializers.class);

            BeanConfig config = new BeanConfig();
            config.setConfigId("nabu-health-swagger-doc");
            config.setTitle("Nabu Health API");
            config.setVersion("v1");
            config.setSchemes(new String[]{"http", "https"});
            config.setBasePath("/health");
            config.setResourcePackage("no.rutebanken.baba.health");
            config.setPrettyPrint(true);
            config.setScan(true);
        }
    }


}
