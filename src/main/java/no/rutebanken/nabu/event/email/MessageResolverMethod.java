/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.event.email;

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
        if (arguments.isEmpty()) {
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