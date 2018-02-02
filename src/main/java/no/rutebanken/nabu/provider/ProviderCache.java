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

package no.rutebanken.nabu.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import no.rutebanken.nabu.exceptions.NabuException;
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

    protected void assertCache() {
        if (cache == null) {
            populate();
            if (cache == null) {
                throw new NabuException("Unable to get user info from organisation registry");
            }
        }
    }

    @Override
    public Collection<Provider> getProviders() {
        assertCache();
        return cache.asMap().values();
    }

    @Override
    public Provider getProvider(Long id) {
        return cache.getIfPresent(id);
    }


}
