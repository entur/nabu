package no.rutebanken.nabu.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {
    /**
     * Hazelcast is set up before spring context because of using second level cache with hazelcast.
     * To be able to use hazelcast instance in other parts of the application, expose it as spring bean here.
     */
    @Bean
    public HazelcastInstance getHazelcastInstanceFromTiamatHazelcastCacheRegionFactory() {
        return NabuHazelcastCacheRegionFactory.getHazelCastInstance();
    }
}
