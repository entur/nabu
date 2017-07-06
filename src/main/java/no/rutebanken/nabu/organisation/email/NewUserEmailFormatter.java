package no.rutebanken.nabu.organisation.email;

import freemarker.template.Configuration;
import no.rutebanken.nabu.organisation.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class NewUserEmailFormatter {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Configuration freemarkerConfiguration;

    @Value("${email.link.password.reset:https://www.dit.no/auth/realms/rutebanken/login-actions/reset-credentials}")
    private String forgotPasswordLink;

    @Value("${email.link.operator:https://operator.rutebanken.org/}")
    private String operatorLink;
    @Value("${email.link.stop.place:https://stoppested.entur.org/}")
    private String stopPlaceLink;
    @Value("${email.link.routedb:https://rutedb.entur.org/}")
    private String routedbLink;
    @Value("${email.link.dev:http://dev.entur.org}")
    private String devLink;
    @Value("${email.link.manual:https://rutebanken.atlassian.net/wiki/pages/viewpage.action?pageId=13729884}")
    private String manualLink;
    @Value("${email.link.netexprofile:https://rutebanken.atlassian.net/wiki/display/PUBLIC/NeTEx+profil+Norge}")
    private String netexProfileLink;
    @Value("${email.link.siriprofile:https://rutebanken.atlassian.net/wiki/display/PUBLIC/SIRI+profil+Norge}")
    private String siriProfileLink;
    @Value("${email.link.stop.place.user.guide:https://rutebanken.atlassian.net/wiki/pages/viewpage.action?pageId=69735716}")
    private String stopPlaceUserGuideLink;


    @Value("${email.contact.info:kollektivdata@entur.org}")
    private String contactInfoEmail;


    public String getSubject(Locale locale) {
        return messageSource.getMessage("new.user.email.subject", new Object[]{}, locale);
    }

    public String formatMessage(User user, Locale locale) {
        Map<String, Object> model = new HashMap<>();

        model.put("user", user);
        model.put("message", new MessageResolverMethod(messageSource, locale));

        model.put("stopPlaceLink", stopPlaceLink);
        model.put("operatorLink", operatorLink);
        model.put("routedbLink", routedbLink);
        model.put("devLink", devLink);
        model.put("manualLink", manualLink);
        model.put("netexProfileLink", netexProfileLink);
        model.put("siriProfileLink", siriProfileLink);
        model.put("forgotPasswordLink", forgotPasswordLink);
        model.put("contactInfoEmail", contactInfoEmail);
        model.put("stopPlaceUserGuideLink", stopPlaceUserGuideLink);

        return geFreeMarkerTemplateContent(model);
    }


    public String geFreeMarkerTemplateContent(Map<String, Object> model) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfiguration.getTemplate("fm_email_new_user_template.ftl"), model);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while processing email template:" + e.getMessage(), e);
        }

    }
}
