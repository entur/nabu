package no.rutebanken.nabu.event.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import no.rutebanken.nabu.exceptions.NabuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.util.Collection;

@Service
public class UserCache implements UserRepository {

    @Autowired
    private UserResource userResource;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${user.cache.max.size:1000}")
    private Integer cacheMaxSize;


    private Cache<String, UserDTO> cache;


    @Scheduled(fixedRateString = "${user.cache.refresh.interval:300000}")
    public void populate() {
        try {
            Cache<String, UserDTO> newCache = CacheBuilder.newBuilder().maximumSize(cacheMaxSize).build();

            userResource.findAll().stream().forEach(user -> newCache.put(user.getUsername(), user));

            cache = newCache;

            logger.info("Updated user cache with result from REST User Service. Cache now has " + cache.size() + " elements");
        } catch (ResourceAccessException re) {
            if (re.getCause() instanceof ConnectException) {
                if (cache == null) {
                    logger.warn("Refresh REST User cache failed:" + re.getMessage() + ". No user info available");
                } else {
                    logger.warn("Refresh REST User cache failed:" + re.getMessage() + ". Could not update user cache, but keeping " + cache.size() + " existing elements.");
                }
            } else {
                throw re;
            }
        }
    }


    @Override
    public Collection<UserDTO> findAll() {
        assertCache();
        return cache.asMap().values();
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
    public UserDTO getByUsername(String username) {
        assertCache();
        return cache.getIfPresent(username);
    }
}
