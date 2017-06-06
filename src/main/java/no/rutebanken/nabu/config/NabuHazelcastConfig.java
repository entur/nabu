package no.rutebanken.nabu.config;

import com.hazelcast.core.HazelcastInstance;
import no.rutebanken.nabu.hazelcast.NabuHazelcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NabuHazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance(@Autowired NabuHazelcastService hazelcastService) {
        return hazelcastService.getHazelcastInstance();
    }

}
