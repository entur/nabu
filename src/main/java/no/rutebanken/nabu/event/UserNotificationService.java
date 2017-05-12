//package no.rutebanken.nabu.event;
//
//
//import no.rutebanken.nabu.domain.event.Event;
//import no.rutebanken.nabu.organisation.model.user.NotificationConfiguration;
//import no.rutebanken.nabu.organisation.model.user.NotificationType;
//import no.rutebanken.nabu.organisation.model.user.User;
//import no.rutebanken.nabu.organisation.repository.UserRepository;
//import no.rutebanken.nabu.repository.EventRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//
//import java.time.Instant;
//import java.util.*;
//
///**
// * For filtering events and sending notifications for a single user.
// */
//@Service
//public class UserNotificationService {
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    private Map<NotificationType, NotificationSender> notificationSenders;
//
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void sendNotificationsForUser(String userId) {
//
//        User user = userRepository.getOneByPublicId(userId);
//        // {transaction per event type?
//        Instant now = Instant.now();
//
//        Map<NotificationType, Set<Event>> events = new HashMap<>();
//        for (NotificationConfiguration notificationConfiguration : user.getNotificationConfigurations()) {
//
//            Set<Event> eventsForNotificationType = events.get(notificationConfiguration.getNotificationType());
//            if (eventsForNotificationType == null) {
//                eventsForNotificationType = new HashSet<>();
//                events.put(notificationConfiguration.getNotificationType(), eventsForNotificationType);
//            }
//            eventsForNotificationType.addAll(eventRepository.findEventMatchingFilter(notificationConfiguration.getEventFilter(), notificationConfiguration.getNotifyFrom(), now));
//
//            notificationConfiguration.setNotifyFrom(now);
//        }
//
//        events.forEach((nType, eventSet) -> notifyUser(user, eventSet, nType));
//
//        userRepository.save(user);
//    }
//
//
//    private void notifyUser(User user, Set<Event> events, NotificationType notificationType) {
//        if (!CollectionUtils.isEmpty(events)) {
//            NotificationSender notificationSender = notificationSenders.get(notificationType);
//            if (notificationSender != null) {
//                notificationSender.sendNotificationsForUser(user, events);
//            } else {
//                logger.warn("No event sender registered for event type: " + notificationType + " Ignored events for user: " + user);
//            }
//        }
//    }
//
//
//}
