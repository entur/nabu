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
    private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter()
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
        if (obj instanceof TemporalAccessor) {
            return FORMATTER.format((TemporalAccessor) obj);
        }
        throw new TemplateModelException("Invalid TemporalAccessor value '" + obj + "'");
    }
}
