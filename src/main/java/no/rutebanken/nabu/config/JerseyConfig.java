/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.config;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.rest.internal.AdminSummaryResource;
import no.rutebanken.nabu.rest.internal.ChangeLogResource;
import no.rutebanken.nabu.rest.internal.LatestUploadResource;
import no.rutebanken.nabu.rest.internal.NotificationResource;
import no.rutebanken.nabu.rest.internal.TimeTableJobEventResource;
import no.rutebanken.nabu.rest.external.TimetableDataDeliveryStatusResource;
import no.rutebanken.nabu.rest.exception.AccessDeniedExceptionMapper;
import no.rutebanken.nabu.rest.exception.NotAuthenticatedExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class JerseyConfig {


    @Bean
    public ServletRegistrationBean<ServletContainer> internalJersey() {
        ServletRegistrationBean<ServletContainer> internalJersey
                = new ServletRegistrationBean<>(new ServletContainer(new InternalServicesConfig()));
        internalJersey.addUrlMappings("/services/events/*");
        internalJersey.setName("InternalJersey");
        internalJersey.setLoadOnStartup(0);
        return internalJersey;
    }


    @Bean
    public ServletRegistrationBean<ServletContainer> externalJersey() {
        ServletRegistrationBean<ServletContainer> externalJersey
                = new ServletRegistrationBean<>(new ServletContainer(new ExternalServicesConfig()));
        externalJersey.addUrlMappings("/services/events-external/*");
        externalJersey.setName("ExternalJersey");
        externalJersey.setLoadOnStartup(0);
        return externalJersey;
    }


    private static class InternalServicesConfig extends ResourceConfig {

        InternalServicesConfig() {
            register(CorsResponseFilter.class);

            register(TimeTableJobEventResource.class);
            register(AdminSummaryResource.class);
            register(LatestUploadResource.class);
            register(NotificationResource.class);
            register(ChangeLogResource.class);

            register(NotAuthenticatedExceptionMapper.class);
            register(AccessDeniedExceptionMapper.class);

            OpenApiResource openApiResource = new OpenApiResource();
            openApiResource.resourcePackages(Set.of("no.rutebanken.nabu.rest.internal"));
            register(openApiResource);
        }

    }

    private static class ExternalServicesConfig extends ResourceConfig {

        ExternalServicesConfig() {
            register(CorsResponseFilter.class);

            register(TimetableDataDeliveryStatusResource.class);

            register(NotAuthenticatedExceptionMapper.class);
            register(AccessDeniedExceptionMapper.class);

            OpenApiResource openApiResource = new OpenApiResource();
            openApiResource.resourcePackages(Set.of("no.rutebanken.nabu.rest.external"));
            register(openApiResource);
        }

    }

}
