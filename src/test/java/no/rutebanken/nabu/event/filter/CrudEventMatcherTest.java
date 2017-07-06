package no.rutebanken.nabu.event.filter;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.*;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.organisation.model.CodeSpace;
import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.responsibility.EntityType;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;
import org.junit.Assert;
import org.junit.Test;

public class CrudEventMatcherTest {

    private static final String STOP_ENTITY_TYPE = "StopPlace";

    private static final String BUS_ENTITY_CLASSIFICATION = "onstreetBus";

    @Test
    public void eventMatchingFilterWithoutAdminZonesSpecificType() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).build();
        Assert.assertTrue(new CrudEventMatcher(filter(BUS_ENTITY_CLASSIFICATION)).matches(event));
    }

    @Test
    public void eventMatchingFilterWithoutAdminZonesWildcardType() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier("whatever").build();
        Assert.assertTrue(new CrudEventMatcher(filter(EntityClassification.ALL_TYPES)).matches(event));
    }


    @Test
    public void eventInAdminZoneMatching() {

        AdministrativeZone zone = adminZone();
        CrudEventFilter filterWithAdminZone = filter(BUS_ENTITY_CLASSIFICATION);
        filterWithAdminZone.getAdministrativeZones().add(zone);


        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).geometry(zone.getPolygon().getCentroid()).build();
        Assert.assertTrue(new CrudEventMatcher(filterWithAdminZone).matches(event));
    }

    @Test
    public void eventOutsideAdminZoneNotMatching() {
        AdministrativeZone zone = adminZone();
        CrudEventFilter filterWithAdminZone = filter(BUS_ENTITY_CLASSIFICATION);
        filterWithAdminZone.getAdministrativeZones().add(zone);

        Point pointOutside=new GeometryFactory().createPoint(new Coordinate(-50,-50));

        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).geometry(pointOutside).build();
        Assert.assertFalse(new CrudEventMatcher(filterWithAdminZone).matches(event));
    }

    @Test
    public void eventWrongTypeNotMatchingFilter() {
        CrudEvent event = CrudEvent.builder().entityType("NotMatchingType").entityClassifier(BUS_ENTITY_CLASSIFICATION).build();
        Assert.assertFalse(new CrudEventMatcher(filter(EntityClassification.ALL_TYPES)).matches(event));
    }

    @Test
    public void eventWrongClassificationNotMatchingFilter() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier("onstreetTram").build();
        Assert.assertFalse(new CrudEventMatcher(filter(BUS_ENTITY_CLASSIFICATION)).matches(event));
    }

    private CrudEventFilter filter(String stopPlaceTypeClassificationCode) {
        CrudEventFilter filter = new CrudEventFilter();

        CodeSpace codeSpace=new CodeSpace("GLB", "glb","url");
        EntityType entityType = new EntityType();
        entityType.setPrivateCode("EntityType");
        entityType.setCodeSpace(codeSpace);
        EntityClassification entityTypeStopPlaceClassification = new EntityClassification();
        entityTypeStopPlaceClassification.setCodeSpace(codeSpace);
        entityTypeStopPlaceClassification.setPrivateCode(STOP_ENTITY_TYPE);
        entityTypeStopPlaceClassification.setEntityType(entityType);

        EntityType stopPlaceType = new EntityType();
        stopPlaceType.setCodeSpace(codeSpace);
        stopPlaceType.setPrivateCode("StopPlaceType");
        EntityClassification stopPlaceTypeClassification = new EntityClassification();
        stopPlaceTypeClassification.setCodeSpace(codeSpace);
        stopPlaceTypeClassification.setEntityType(stopPlaceType);
        stopPlaceTypeClassification.setPrivateCode(stopPlaceTypeClassificationCode);

        filter.setEntityClassifications(Sets.newHashSet(stopPlaceTypeClassification, entityTypeStopPlaceClassification));
        return filter;
    }


    private AdministrativeZone adminZone() {
        AdministrativeZone zone = new AdministrativeZone();
        GeometryFactory fact = new GeometryFactory();
        LinearRing linear = new GeometryFactory().createLinearRing(new Coordinate[]{new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)});
        zone.setPolygon(new Polygon(linear, null, fact));

        return zone;
    }
}
