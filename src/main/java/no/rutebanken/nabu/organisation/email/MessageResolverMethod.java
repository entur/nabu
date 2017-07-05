package no.rutebanken.nabu.organisation.email;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 * For resolving messages from resource bundle from freemarker templates.
 */
public class MessageResolverMethod implements TemplateMethodModelEx {

    private MessageSource messageSource;

    private Locale locale;

    public MessageResolverMethod(MessageSource messageSource, Locale locale) {
        this.locale = locale;
        this.messageSource = messageSource;
    }


    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() < 1) {
            throw new TemplateModelException("Wrong number of arguments");
        }
        SimpleScalar arg = (SimpleScalar) arguments.get(0);
        String code = arg.getAsString();
        if (code == null || code.isEmpty()) {
            throw new TemplateModelException("Invalid code value '" + code + "'");
        }

        Object[] argsArray = null;
        if (arguments.size() > 1) {
            argsArray = arguments.subList(1, arguments.size()).toArray();
        }

        return messageSource.getMessage(code, argsArray, code, locale);
    }
}