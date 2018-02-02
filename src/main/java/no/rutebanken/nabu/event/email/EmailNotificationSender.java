/*
 *
 *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *
 */

package no.rutebanken.nabu.event.email;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.event.NotificationProcessor;
import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import no.rutebanken.nabu.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

/**
 * Log notifications to user (for test purposes).
 */
@Service
public class EmailNotificationSender implements NotificationProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailNotificationFormatter formatter;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.from:varsel@entur.no}")
    private String emailFrom;


    @Value("${notification.email.language.default:en}")
    private String emailLanguageDefault;

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Override
    public void processNotificationsForUser(UserDTO user, Set<Notification> notifications) {

        if (user.getContactDetails() == null || user.getContactDetails().getEmail() == null) {
            logger.warn("Unable to notify user without registered email address: " + user.getUsername() + ". Discarding notifications: " + notifications);
            notificationRepository.delete(notifications);
            return;
        }

        logger.info("Sending email to user: " + user.getUsername() + " for notifications: " + notifications);

        Locale locale = new Locale(emailLanguageDefault); // TODO get users default from user

        sendEmail(user.getContactDetails().getEmail(), formatter.getSubject(locale), formatter.formatMessage(notifications, locale));

        notifications.forEach(n -> n.setStatus(Notification.NotificationStatus.COMPLETE));
        notificationRepository.save(notifications);
    }

    protected void sendEmail(String to, String subject, String msg) {
        if (emailEnabled) {
            mailSender.send(mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setText(msg, true);
                helper.setSubject(subject);
                helper.setTo(to);
                helper.setFrom(emailFrom);
            });
        } else {
            logger.warn("Email disabled, not sending: " + msg);
        }
    }


    @Override
    public Set<NotificationType> getSupportedNotificationTypes() {
        return Sets.newHashSet(NotificationType.EMAIL, NotificationType.EMAIL_BATCH);
    }
}
