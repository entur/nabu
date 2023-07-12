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

import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class TemporalAccessorFormatter implements TemplateMethodModelEx {
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter()
                                                               .withZone(ZoneId.of("Europe/Oslo"));


    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (CollectionUtils.isEmpty(arguments)) {
            return "";
        }

        if (arguments.size() > 1) {
            throw new TemplateModelException("Wrong number of arguments");
        }
        StringModel arg = (StringModel) arguments.get(0);
        Object obj = arg.getWrappedObject();
        if (obj == null) {
            return "";
        }
        if (obj instanceof TemporalAccessor temporalAccessor) {
            return FORMATTER.format(temporalAccessor);
        }
        throw new TemplateModelException("Invalid TemporalAccessor value '" + obj + "'");
    }
}
