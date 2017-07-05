package no.rutebanken.nabu.organisation.email;

import no.rutebanken.nabu.organisation.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NewUserEmailSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NewUserEmailFormatter newUserEmailFormatter;

    @Value("${new.user.email.from:noreply@entur.no}")
    private String emailFrom;

    @Value("${new.user.email.language.default:no}")
    private String emailLanguageDefault;


    @Value("${new.user.email.enabled:true}")
    private boolean sendEmailEnabled;

    public void sendEmail(User user) {
        Locale locale = new Locale(emailLanguageDefault); // TODO get users default from user
        sendEmail(user.getContactDetails().getEmail(), newUserEmailFormatter.getSubject(locale), newUserEmailFormatter.formatMessage(user, locale));
    }


    protected void sendEmail(String to, String subject, String msg) {
        if (sendEmailEnabled) {
            mailSender.send(mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setText(msg, true);
                helper.setSubject(subject);
                helper.setTo(to);
                helper.setFrom(emailFrom);
            });
            logger.info("Sent email with account information to: " + to);
        } else {
            logger.info("Not sending email to new user: " + to + " as this has been disabled");
        }
    }

}
