package no.rutebanken.nabu.config;

import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.organisation.rest.*;
import no.rutebanken.nabu.organisation.rest.exception.*;
import no.rutebanken.nabu.rest.*;
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
        register(TimeTableJobEventResource.class);
        register(SystemJobResource.class);
        register(DataDeliveryStatusResource.class);
        register(ProviderResource.class);
        register(ApplicationStatusResource.class);
        register(NotificationResource.class);
        register(CorsResponseFilter.class);

        register(CodeSpaceResource.class);
        register(OrganisationResource.class);
        register(AdministrativeZoneResource.class);
        register(UserResource.class);
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
    }

}
