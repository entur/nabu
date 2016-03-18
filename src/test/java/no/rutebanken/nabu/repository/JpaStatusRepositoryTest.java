package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.App;
import no.rutebanken.nabu.domain.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(App.class)
public class JpaStatusRepositoryTest {

    @Autowired
    StatusRepository repository;

    @Test
    public void testUpdate() throws Exception {
        Status input = new Status("00013-gtfs.zip", 2L, Status.Action.IMPORT, Status.State.OK, "1234567");
        Status result = repository.update(input);
        assertThat(result).isEqualToComparingFieldByField(input);
    }

    @Test
    public void testGetStatusForProvider() throws Exception {
        Collection<Status> statuses = repository.getStatusForProvider(2L);
        assertThat(statuses).hasSize(2);
    }

}