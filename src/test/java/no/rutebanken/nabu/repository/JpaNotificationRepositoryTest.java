package no.rutebanken.nabu.repository;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.*;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.Instant;

public class JpaNotificationRepositoryTest extends BaseIntegrationTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void clearAllRemovesAllNotificationsForJobDomain() {
        JobEvent matchingEvent = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).providerId(2L).referential("ost").state(JobState.OK).name("file1.zip").externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(Instant.now()).build();
        JobEvent otherDomainEvent = JobEvent.builder().domain("otherDomain").providerId(2L).referential("ost").state(JobState.OK).name("file1.zip").externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(Instant.now()).build();
        CrudEvent crudEvent = CrudEvent.builder().entityType("type").entityClassifier("classifier").version(1l).externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(Instant.now()).build();
        eventRepository.save(Sets.newHashSet(matchingEvent, otherDomainEvent, crudEvent));

        Notification matchingEventNotification = new Notification("user1", NotificationType.WEB, matchingEvent);
        Notification otherDomainEventNotification = new Notification("user1", NotificationType.WEB, otherDomainEvent);
        Notification crudEventNotification = new Notification("user1", NotificationType.WEB, crudEvent);
        notificationRepository.save(Sets.newHashSet(matchingEventNotification, otherDomainEventNotification, crudEventNotification));


        notificationRepository.clearAll(matchingEvent.getDomain());
        entityManager.clear();

        Assert.assertNull(notificationRepository.findOne(matchingEventNotification.getPk()));
        Assert.assertNotNull(notificationRepository.findOne(otherDomainEventNotification.getPk()));
        Assert.assertNotNull(notificationRepository.findOne(crudEventNotification.getPk()));
    }

    @Test
    public void clearRemovesAllNotificationsForJobDomainAndProvider() {
        JobEvent matchingEvent = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).providerId(2L).referential("ost").state(JobState.OK).name("file1.zip").externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(Instant.now()).build();
        JobEvent otherProviderEvent = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).providerId(666L).referential("ost").state(JobState.OK).name("file1.zip").externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(Instant.now()).build();
        JobEvent otherDomainEvent = JobEvent.builder().domain("otherDomain").providerId(2L).referential("ost").state(JobState.OK).name("file1.zip").externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(Instant.now()).build();
        CrudEvent crudEvent = CrudEvent.builder().entityType("type").entityClassifier("classifier").version(1l).externalId("1").action(TimeTableAction.IMPORT).correlationId("corr-id-1").eventTime(Instant.now()).build();
        eventRepository.save(Sets.newHashSet(matchingEvent, otherProviderEvent, otherDomainEvent, crudEvent));

        Notification matchingEventNotification = new Notification("user1", NotificationType.WEB, matchingEvent);
        Notification otherProviderEventNotification = new Notification("user1", NotificationType.WEB, otherProviderEvent);
        Notification otherDomainEventNotification = new Notification("user1", NotificationType.WEB, otherDomainEvent);
        Notification crudEventNotification = new Notification("user1", NotificationType.WEB, crudEvent);
        notificationRepository.save(Sets.newHashSet(matchingEventNotification, otherProviderEventNotification, otherDomainEventNotification, crudEventNotification));


        notificationRepository.clear(matchingEvent.getDomain(), matchingEvent.getProviderId());
        entityManager.clear();

        Assert.assertNull(notificationRepository.findOne(matchingEventNotification.getPk()));
        Assert.assertNotNull(notificationRepository.findOne(otherProviderEventNotification.getPk()));
        Assert.assertNotNull(notificationRepository.findOne(otherDomainEventNotification.getPk()));
        Assert.assertNotNull(notificationRepository.findOne(crudEventNotification.getPk()));
    }

}
