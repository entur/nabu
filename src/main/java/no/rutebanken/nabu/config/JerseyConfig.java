package no.rutebanken.nabu.config;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.rest.ApplicationStatusResource;
import no.rutebanken.nabu.rest.CrudEventResource;
import no.rutebanken.nabu.rest.DataDeliveryStatusResource;
import no.rutebanken.nabu.rest.FileUploadResource;
import no.rutebanken.nabu.rest.HazelcastResource;
import no.rutebanken.nabu.rest.NotificationResource;
import no.rutebanken.nabu.rest.SystemJobResource;
import no.rutebanken.nabu.rest.TimeTableJobEventResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MultiPartFeature.class);
        register(CorsResponseFilter.class);

        register(ApplicationStatusResource.class);

        register(FileUploadResource.class);
        register(TimeTableJobEventResource.class);
        register(SystemJobResource.class);
        register(DataDeliveryStatusResource.class);
        register(NotificationResource.class);
        register(CrudEventResource.class);

        configureSwagger();
    }


    private void configureSwagger() {
        // Available at localhost:port/api/swagger.json
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("nabu-swagger-doc");
        config.setTitle("Nabu API");
        config.setVersion("v1");
        config.setSchemes(new String[]{"http", "https"});
        config.setBasePath("/jersey");
        config.setResourcePackage("no.rutebanken.nabu");
        config.setPrettyPrint(true);
        config.setScan(true);
    }

}
