package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.App;
import no.rutebanken.nabu.domain.ChouetteInfo;
import no.rutebanken.nabu.domain.Provider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(App.class)
@IntegrationTest
public class JpaProviderRepositoryTest {

    @Autowired
    ProviderRepository repository;

    @Test
    public void testGetProviders() {
        Collection<Provider> providers = repository.getProviders();
        assertThat(providers).hasSize(2);
    }

    @Test
    public void testGetProviderById() {
        Provider provider = repository.getProvider(42L);
        assertThat(provider).isEqualTo(new Provider(42L, "Flybussekspressen", "42",
                new ChouetteInfo(1L, "flybussekspressen", "flybussekspressen", "Rutebanken", "admin@rutebanken.org")));
    }

}