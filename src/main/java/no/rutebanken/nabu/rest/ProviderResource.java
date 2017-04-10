package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.Collection;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_ADMIN;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_EDIT;


@Component
@Produces("application/json")
@Path("/providers")
public class ProviderResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProviderRepository providerRepository;

    @GET
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public Provider getProvider(@PathParam("providerId") Long providerId) {
        logger.debug("Returning provider with id '" + providerId + "'");
        return providerRepository.getProvider(providerId);
    }

    @DELETE
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public void deleteProvider(@PathParam("providerId") Long providerId) {
        logger.info("Deleting provider with id '" + providerId + "'");
        providerRepository.deleteProvider(providerId);
    }

    @PUT
    @Path("/update")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#provider.id)")
    public void updateProvider(Provider provider) {
        logger.info("Updating provider " + provider);
        providerRepository.updateProvider(provider);
    }

    @POST
    @Path("/create")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#provider.id)")
    public Provider createProvider(Provider provider) {
        logger.info("Creating provider " + provider);
        return providerRepository.createProvider(provider);
    }

    @GET
    @Path("/all")
    @Deprecated
    public Collection<Provider> getAllProviders() {
        return getProviders();
    }

    @GET
    public Collection<Provider> getProviders() {
        logger.debug("Returning all providers.");
        return providerRepository.getProviders();
    }

}
