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
import no.rutebanken.nabu.rest.AdminSummaryResource;
import no.rutebanken.nabu.rest.ChangeLogResource;
import no.rutebanken.nabu.rest.LatestUploadResource;
import no.rutebanken.nabu.rest.NotificationResource;
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
    public ServletRegistrationBean<ServletContainer> publicJersey() {
        ServletRegistrationBean<ServletContainer> publicJersey
                = new ServletRegistrationBean<>(new ServletContainer(new ServicesConfig()));
        publicJersey.addUrlMappings("/services/events/*");
        publicJersey.setName("PublicJersey");
        publicJersey.setLoadOnStartup(0);
        publicJersey.getInitParameters().put("swagger.scanner.id", "events-scanner");
        publicJersey.getInitParameters().put("swagger.config.id","events-swagger-doc");
        return publicJersey;
    }

    private static class ServicesConfig extends ResourceConfig {

        public ServicesConfig() {
            register(CorsResponseFilter.class);

            register(TimeTableJobEventResource.class);
            register(AdminSummaryResource.class);
            register(LatestUploadResource.class);
            register(NotificationResource.class);
            register(ChangeLogResource.class);

            register(NotAuthenticatedExceptionMapper.class);
            register(AccessDeniedExceptionMapper.class);

            register(OpenApiResource.class);
        }

    }

}
