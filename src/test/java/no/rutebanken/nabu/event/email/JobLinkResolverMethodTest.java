package no.rutebanken.nabu.event.email;

import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateModelException;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class JobLinkResolverMethodTest {

    private static final String BASE_URL = "http://test/";

    private JobLinkResolverMethod jobLinkResolverMethod = new JobLinkResolverMethod(BASE_URL);

    @Test
    public void testImportLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("rb_ref").action(TimeTableAction.IMPORT.name()).externalId("7").build();
        assertLink(event, "rb_ref/imports/7/compliance_check");
    }

    @Test
    public void testExportLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("rb_ref").action(TimeTableAction.EXPORT.name()).externalId("7").build();
        assertLink(event, "rb_ref/exports/7/compliance_check");
    }

    @Test
    public void testExportNetexLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("rb_ref").action(TimeTableAction.EXPORT_NETEX.name()).externalId("7").build();
        assertLink(event, "rb_ref/exports/7/compliance_check");
    }

    @Test
    public void testValidationL1Link() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("rb_ref").action(TimeTableAction.VALIDATION_LEVEL_1.name()).externalId("7").build();
        assertLink(event, "rb_ref/compliance_checks/7/report");
    }

    @Test
    public void testValidationL2Link() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("rb_ref").action(TimeTableAction.VALIDATION_LEVEL_2.name()).externalId("7").build();
        assertLink(event, "rb_ref/compliance_checks/7/report");
    }

    @Test
    public void testFileClassificationYieldsNoLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("rb_ref").action(TimeTableAction.FILE_CLASSIFICATION.name()).externalId("7").build();
        assertLink(event, null);
    }


    @Test
    public void testMissingExternalIdYieldsNoLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("rb_ref").action(TimeTableAction.IMPORT.name()).externalId(null).build();
        assertLink(event, null);
    }

    @Test
    public void testMissingReferentialYieldsNoLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential(null).action(TimeTableAction.IMPORT.name()).externalId("id").build();
        assertLink(event, null);
    }

    @Test
    public void testMissingActionYieldsNoLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).referential("ref").externalId("id").build();
        assertLink(event, null);
    }

    @Test
    public void testMissingDomainYieldsNoLink() {
        JobEvent event = JobEvent.builder().referential("ref").action(TimeTableAction.IMPORT.name()).externalId("id").build();
        assertLink(event, null);
    }

    @Test
    public void testNonTimetableDomainYieldsNoLink() {
        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.GEOCODER).referential("ref").action(TimeTableAction.IMPORT.name()).externalId("id").build();
        assertLink(event, null);
    }

    private void assertLink(JobEvent jobEvent, String relativeExpectedLink) {
        try {
            Object link = jobLinkResolverMethod.exec(Arrays.asList(new BeanModel(jobEvent, new SimpleObjectWrapper())));
            if (relativeExpectedLink == null) {
                Assert.assertNull(link);
            } else {
                Assert.assertEquals(BASE_URL + relativeExpectedLink, link);
            }
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }
    }


}
