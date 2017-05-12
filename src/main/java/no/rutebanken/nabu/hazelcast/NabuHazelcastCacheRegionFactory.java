package no.rutebanken.nabu.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.hibernate.cache.spi.RegionFactory;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NabuHazelcastCacheRegionFactory extends com.hazelcast.hibernate.HazelcastCacheRegionFactory implements RegionFactory {

    private static final Logger logger = LoggerFactory.getLogger(NabuHazelcastCacheRegionFactory.class);

    private static final NabuHazelcastService extendedHazelcastService = initHazelcastService();

    private static NabuHazelcastService initHazelcastService() {

        try {
            logger.info("initHazelcastService");
            String kubernetesUrl = getProperty("rutebanken.kubernetes.url", false);

            boolean kubernetesEnabled = getBooleanProperty("rutebanken.kubernetes.enabled", false);
            String namespace = getProperty("rutebanken.kubernetes.namespace", false);
            String hazelcastManagementUrl = getProperty("rutebanken.hazelcast.management.url", false);

            KubernetesService kubernetesService = new KubernetesService(kubernetesUrl, namespace, kubernetesEnabled);
            logger.info("Created kubernetes service");
            NabuHazelcastService extendedHazelcastService = new NabuHazelcastService(kubernetesService, hazelcastManagementUrl);
            logger.info("Init ExtendedHazelcastService");
            extendedHazelcastService.init();
            logger.info(extendedHazelcastService.information());
            return extendedHazelcastService;

        } catch (Exception e) {
            throw new RuntimeException("Error initializing hazelcast service", e);
        }
    }

    public static HazelcastInstance getHazelCastInstance() {
        return extendedHazelcastService.getHazelcastInstance();
    }

    private static String getProperty(String key, boolean required) {
        String value = System.getProperty(key);
        logger.info("Loaded {}: {}", key, value);
        if(required && value == null) {
            throw new RuntimeException("Property " + key + " cannot be null");
        }
        return String.valueOf(value);
    }

    private static boolean getBooleanProperty(String key, boolean required) {
        String value = getProperty(key, required);
        boolean booleanValue = value.equalsIgnoreCase("true");
        return booleanValue;
    }

    /**
     * Must be configured in properties file. Like this:
     * spring.jpa.properties.hibernate.cache.region.factory_class=no.rutebanken.nabu.hazelcast.NabuHazelcastCacheRegionFactory
     */
    public NabuHazelcastCacheRegionFactory() {
        super(extendedHazelcastService.getHazelcastInstance());
        logger.info("Created factory with: {}", getHazelcastInstance());
    }

}
