package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;


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
        logger.info("Returning provider with id '" + providerId + "'");
        return providerRepository.getProvider(providerId);
    }

    @GET
    @Path("/all")
    public Collection<Provider> getProviders() {
        logger.info("Returning all providers.");
        return providerRepository.getProviders();
    }

}
