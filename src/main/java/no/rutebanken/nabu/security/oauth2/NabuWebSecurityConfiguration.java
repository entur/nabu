package no.rutebanken.nabu.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Authentication and authorization configuration for Nabu.
 * All requests must be authenticated except for the Swagger and Actuator endpoints.
 */
@Profile("!test")
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class NabuWebSecurityConfiguration {

    /**
     * The one operation whose OpenAPI contract declares an error body. Kept here as the servlet
     * path, because Spring Security sees the request before Jersey has stripped its own mapping.
     * <p>
     * Package-private so that what it does and does not match can be asserted: everything this
     * matcher lets through keeps the 401 it has always had, so its exact reach is the boundary
     * between the new endpoint and every older one.
     */
    static final RequestMatcher PROGRESS_ENDPOINT =
            PathPatternRequestMatcher.withDefaults().matcher("/services/events-external/progress/**");

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Authorization", "x-correlation-id", "Et-Client-Name", "sentry-trace", "baggage"));
        configuration.addAllowedOrigin("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           MultiIssuerAuthenticationManagerResolver multiIssuerAuthenticationManagerResolver,
                                           ObjectMapper objectMapper) throws Exception {
        // Only the progress endpoint declares problem+json, so only the progress endpoint gets it.
        // Every other path keeps the body Spring Security has always sent — which is none — because
        // an empty 401 is what the services and the operations UI calling this application were
        // written against, and a 401 is not where to find out otherwise.
        DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(
                new LinkedHashMap<>(Map.of(PROGRESS_ENDPOINT, new ProblemDetailAuthenticationEntryPoint(objectMapper))));
        entryPoint.setDefaultEntryPoint(new BearerTokenAuthenticationEntryPoint());

        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/services/events/openapi.json").permitAll()
                        .requestMatchers("/services/events-external/openapi.json").permitAll()
                        .requestMatchers("/actuator/prometheus").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/health/liveness").permitAll()
                        .requestMatchers("/actuator/health/readiness").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(configurer -> configurer
                        .authenticationEntryPoint(entryPoint)
                        .authenticationManagerResolver(multiIssuerAuthenticationManagerResolver))
                .oauth2Client(withDefaults());
        return http.build();
    }


}
