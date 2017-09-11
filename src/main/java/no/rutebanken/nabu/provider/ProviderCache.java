package no.rutebanken.nabu.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import no.rutebanken.nabu.provider.model.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.util.Collection;


@Repository
public class ProviderCache implements ProviderRepository {

    @Autowired
    private ProviderResource restProviderService;

    @Value("${provider.cache.max.size:100}")
    private Integer cacheMaxSize;

    private static Cache<Long, Provider> cache;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedRateString = "${provider.cache.refresh.interval:300000}")
    public void populate() {
        try {
            Cache<Long, Provider> newCache = CacheBuilder.newBuilder().maximumSize(cacheMaxSize).build();
            restProviderService.getProviders().stream().forEach(provider -> newCache.put(provider.getId(), provider));

            cache = newCache;
            logger.info("Updated provider cache with result from REST Provider Service. Cache now has " + cache.size() + " elements");
        } catch (ResourceAccessException re) {
            if (re.getCause() instanceof ConnectException) {
                if (cache == null) {
                    logger.warn("Refresh REST provider cache failed:" + re.getMessage() + ". No provider info available");
                } else {
                    logger.warn("Refresh REST provider cache failed:" + re.getMessage() + ". Could not update provider cache, but keeping " + cache.size() + " existing elements.");
                }
            } else {
                throw re;
            }
        }
    }


    @Override
    public Collection<Provider> getProviders() {
        return cache.asMap().values();
    }

    @Override
    public Provider getProvider(Long id) {
        return cache.getIfPresent(id);
    }


}
