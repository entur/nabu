package no.rutebanken.nabu.config;

import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.organization.AdministrativeZone;
import no.rutebanken.nabu.organization.model.organization.Organisation;
import no.rutebanken.nabu.organization.rest.*;
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
		register(StatusResource.class);
		register(SystemJobResource.class);
		register(ProviderResource.class);
		register(ApplicationStatusResource.class);
		register(CorsResponseFilter.class);

		register(CodeSpaceResource.class);
		register(OrganisationResource.class);
		register(AdministrativeZoneResource.class);
		register(UserResource.class);
		register(RoleResource.class);
		register(EntityTypeResource.class);
		register(EntityClassificationResource.class);
		register(ResponsibilitySetResource.class);
	}

}
