package no.rutebanken.nabu.event.email;

import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.TimeTableAction;

import java.util.List;

public class JobLinkResolverMethod implements TemplateMethodModelEx {

    private String baseUrl;

    public JobLinkResolverMethod(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() < 1) {
            throw new TemplateModelException("Wrong number of arguments");
        }
        Object obj = ((BeanModel) arguments.get(0)).getWrappedObject();
        if (!(obj instanceof JobEvent)) {
            throw new TemplateModelException("Wrong type of argument");
        }
        JobEvent jobEvent = (JobEvent) obj;

        if (JobEvent.JobDomain.TIMETABLE.name().equals(jobEvent.getDomain())) {
            return formatTimetableJobLink(jobEvent);
        }

        return null;
    }

    private String formatTimetableJobLink(JobEvent jobEvent) {
        if (jobEvent.getAction() == null || jobEvent.getExternalId() == null || jobEvent.getReferential() == null) {
            return null;
        }
        String resource = null;
        if (jobEvent.getAction().equals(TimeTableAction.IMPORT.name())) {
            resource = "imports/" + jobEvent.getExternalId() + "/compliance_check";
        } else if (jobEvent.getAction().equals(TimeTableAction.EXPORT.name()) || jobEvent.getAction().equals(TimeTableAction.EXPORT_NETEX.name())) {
            resource = "exports/" + jobEvent.getExternalId() + "/compliance_check";
        } else if (jobEvent.getAction().equals(TimeTableAction.VALIDATION_LEVEL_1.name())
                           || jobEvent.getAction().equals(TimeTableAction.VALIDATION_LEVEL_2.name())) {
            resource = "compliance_checks/" + jobEvent.getExternalId() + "/report";
        }

        if (resource == null) {
            return null;
        }


        return baseUrl + jobEvent.getReferential() + "/" + resource;
    }
}
