package no.rutebanken.nabu.config;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.organisation.rest.AdministrativeZoneResource;
import no.rutebanken.nabu.organisation.rest.CodeSpaceResource;
import no.rutebanken.nabu.organisation.rest.EntityClassificationResource;
import no.rutebanken.nabu.organisation.rest.EntityTypeResource;
import no.rutebanken.nabu.organisation.rest.NotificationConfigurationResource;
import no.rutebanken.nabu.organisation.rest.OrganisationResource;
import no.rutebanken.nabu.organisation.rest.ResponsibilitySetResource;
import no.rutebanken.nabu.organisation.rest.RoleResource;
import no.rutebanken.nabu.organisation.rest.UserResource;
import no.rutebanken.nabu.organisation.rest.exception.AccessDeniedExceptionMapper;
import no.rutebanken.nabu.organisation.rest.exception.IllegalArgumentExceptionMapper;
import no.rutebanken.nabu.organisation.rest.exception.NotAuthenticatedExceptionMapper;
import no.rutebanken.nabu.organisation.rest.exception.OrganisationExceptionMapper;
import no.rutebanken.nabu.organisation.rest.exception.PersistenceExceptionMapper;
import no.rutebanken.nabu.organisation.rest.exception.SpringExceptionMapper;
import no.rutebanken.nabu.rest.ApplicationStatusResource;
import no.rutebanken.nabu.rest.CrudEventResource;
import no.rutebanken.nabu.rest.DataDeliveryStatusResource;
import no.rutebanken.nabu.rest.HazelcastResource;
import no.rutebanken.nabu.rest.NotificationResource;
import no.rutebanken.nabu.rest.ProviderResource;
import no.rutebanken.nabu.rest.SystemJobResource;
import no.rutebanken.nabu.rest.TimeTableJobEventResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jersey")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(TimeTableJobEventResource.class);
        register(SystemJobResource.class);
        register(DataDeliveryStatusResource.class);
        register(ProviderResource.class);
        register(ApplicationStatusResource.class);
        register(HazelcastResource.class);
        register(NotificationResource.class);
        register(CrudEventResource.class);
        register(CorsResponseFilter.class);

        register(CodeSpaceResource.class);
        register(OrganisationResource.class);
        register(AdministrativeZoneResource.class);
        register(UserResource.class);
        register(NotificationConfigurationResource.class);
        register(RoleResource.class);
        register(EntityTypeResource.class);
        register(EntityClassificationResource.class);
        register(ResponsibilitySetResource.class);

        register(NotAuthenticatedExceptionMapper.class);
        register(PersistenceExceptionMapper.class);
        register(SpringExceptionMapper.class);
        register(IllegalArgumentExceptionMapper.class);
        register(AccessDeniedExceptionMapper.class);
        register(OrganisationExceptionMapper.class);

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
