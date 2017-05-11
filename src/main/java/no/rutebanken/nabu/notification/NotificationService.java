package no.rutebanken.nabu.notification;

import no.rutebanken.nabu.organisation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserNotificationService userNotificationService;

    @Transactional(readOnly = true)
    public void sendNotificationsForAllUsers() {
        userRepository.findAll().forEach(u -> sendNotificationsForUser(u.getId()));
    }


    public void sendNotificationsForUser(String userId) {
        userNotificationService.sendNotificationsForUser(userId);
    }
}
