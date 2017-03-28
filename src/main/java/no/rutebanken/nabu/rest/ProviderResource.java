package no.rutebanken.nabu.rest;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.repository.ProviderRepository;


@Component
@Produces("application/json")
@Path("/providers")
public class ProviderResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProviderRepository providerRepository;

    @GET
    @Path("/{providerId}")
    public Provider getProvider(@PathParam("providerId") Long providerId) {
        logger.debug("Returning provider with id '" + providerId + "'");
        return providerRepository.getProvider(providerId);
    }

    @DELETE
    @Path("/{providerId}")
    public void deleteProvider(@PathParam("providerId") Long providerId) {
        logger.info("Deleting provider with id '" + providerId + "'");
        providerRepository.deleteProvider(providerId);
    }

    @PUT
    @Path("/update")
    public void updateProvider(Provider provider) {
        logger.info("Updating provider "+provider);
        providerRepository.updateProvider(provider);
    }

    @POST
    @Path("/create")
    public Provider createProvider(Provider provider) {
        logger.info("Creating provider "+provider);
        return providerRepository.createProvider(provider);
    }

    @GET
    @Path("/all")
    public Collection<Provider> getProviders() {
        logger.debug("Returning all providers.");
        return providerRepository.getProviders();
    }

}
