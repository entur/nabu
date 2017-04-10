package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class JpaStatusRepositoryTest extends BaseIntegrationTest {

    @Autowired
    StatusRepository repository;

    private Date now = new Date();

    @Test
    public void testUpdate() throws Exception {
        Status input = new Status("00013-gtfs.zip", 2L, 1L, Status.Action.IMPORT, Status.State.OK, "1234567", now, "ost");
        repository.add(input);
    }

    @Test
    public void testGetStatusForProvider() throws Exception {
        Collection<Status> statuses = repository.getStatusForProvider(2L, null, null, null, null, null, null);
        assertThat(statuses).hasSize(3);
    }


    @Test
    public void testGetStatusWithAllCriteria() throws Exception {
        Status s1 = new Status("file1.zip", 3L, 1L, Status.Action.IMPORT, Status.State.OK, "corr-id-1", now, "ost");
        repository.add(s1);
        Status s2 = new Status("file1.zip", 3L, 2L, Status.Action.EXPORT, Status.State.FAILED, "corr-id-1", DateUtils.addMinutes(now, 1), "ost");
        repository.add(s2);
        Status s3 = new Status("file2.zip", 3L, 1L, Status.Action.IMPORT, Status.State.TIMEOUT, "corr-id-2", now, "ost");
        repository.add(s3);

        Collection<Status> statusesQueryMatchingS1 = repository.getStatusForProvider(3L, now, now, Arrays.asList(Status.Action.IMPORT), Arrays.asList(Status.State.OK), Arrays.asList(1l), Arrays.asList("file1.zip"));
        assertThat(statusesQueryMatchingS1).hasSize(2);
        assertThat(statusesQueryMatchingS1).contains(s1, s2);


        Collection<Status> statusesQueryMatchingS1andS3 = repository.getStatusForProvider(3L, now, now, Arrays.asList(Status.Action.IMPORT, Status.Action.EXPORT),
                Arrays.asList(Status.State.OK, Status.State.TIMEOUT), null, Arrays.asList("file1.zip", "file2.zip"));
        assertThat(statusesQueryMatchingS1andS3).hasSize(3);
    }


}