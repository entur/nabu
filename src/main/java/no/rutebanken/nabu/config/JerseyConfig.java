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

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import no.rutebanken.nabu.filter.CorsResponseFilter;
import no.rutebanken.nabu.health.rest.HealthResource;
import no.rutebanken.nabu.rest.ChangeLogResource;
import no.rutebanken.nabu.rest.LatestUploadResource;
import no.rutebanken.nabu.rest.NotificationResource;
import no.rutebanken.nabu.rest.AdminSummaryResource;
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
        publicJersey.addUrlMappings("/services/events/*");
        publicJersey.setName("PublicJersey");
        publicJersey.setLoadOnStartup(0);
        publicJersey.getInitParameters().put("swagger.scanner.id", "events-scanner");
        publicJersey.getInitParameters().put("swagger.config.id","events-swagger-doc");
        return publicJersey;
    }

    @Bean
    public ServletRegistrationBean privateJersey() {
        ServletRegistrationBean privateJersey
                = new ServletRegistrationBean(new ServletContainer(new HealthConfig()));
        privateJersey.addUrlMappings("/health/*");
        privateJersey.setName("PrivateJersey");
        privateJersey.setLoadOnStartup(0);
        privateJersey.getInitParameters().put("swagger.scanner.id", "health-scanner");
        privateJersey.getInitParameters().put("swagger.config.id","nabu-health-swagger-doc");
        return privateJersey;
    }


    private class ServicesConfig extends ResourceConfig {

        public ServicesConfig() {
            register(CorsResponseFilter.class);

            register(TimeTableJobEventResource.class);
            register(AdminSummaryResource.class);
            register(LatestUploadResource.class);
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
            config.setResourcePackage("no.rutebanken.nabu.rest");
            config.setPrettyPrint(true);
            config.setScan(true);
            config.setScannerId("events-scanner");
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
            config.setResourcePackage("no.rutebanken.baba.health");
            config.setPrettyPrint(true);
            config.setScan(true);
            config.setScannerId("health-scanner");
        }
    }


}
