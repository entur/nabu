package no.rutebanken.nabu.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import no.rutebanken.nabu.domain.ChouetteInfo;
import no.rutebanken.nabu.domain.Provider;


@RunWith(SpringRunner.class)
@SpringBootTest
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
                new ChouetteInfo(1L, "flybussekspressen","http://www.ns.1", "flybussekspressen", "Rutebanken", "admin@rutebanken.org")));
    }


    @Test
    public void testCreateAndUpdateAndDeleteProvider() {
    	
    	ChouetteInfo chouetteInfo = new ChouetteInfo(null,"xmlns","xmlnsurl","refe","org","user");
		Provider newProvider = new Provider(null,"junit provider","sftpAccount",chouetteInfo );
		repository.createProvider(newProvider);
		
		Provider providerForUpdate = repository.getProvider(newProvider.id);
    	providerForUpdate.sftpAccount = "modified";
    	
    	repository.updateProvider(providerForUpdate);
		Provider providerForVerification = repository.getProvider(newProvider.id);
    	
		Assert.assertEquals(providerForUpdate.sftpAccount, providerForVerification.sftpAccount);
    	
		repository.deleteProvider(newProvider.id);
		
		Provider noProvider = repository.getProvider(newProvider.id);
		
		Assert.assertNull(noProvider);
    }

}