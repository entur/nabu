package no.rutebanken.nabu.event.filter;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.*;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.responsibility.EntityType;
import no.rutebanken.nabu.organisation.model.user.eventfilter.CrudEventFilter;
import org.junit.Assert;
import org.junit.Test;

public class CrudEventMatcherTest {

    private static final String STOP_ENTITY_TYPE = "StopPlace";

    private static final String POI_ENTITY_TYPE = "PlaceOfInterest";

    private static final String BUS_ENTITY_CLASSIFICATION = "onstreetBus";

    @Test
    public void eventMatchingFilterWithoutAdminZonesSpecificType() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).build();
        Assert.assertTrue(new CrudEventMatcher(filter()).matches(event));
    }

    @Test
    public void eventMatchingFilterWithoutAdminZonesWildcardType() {
        CrudEvent event = CrudEvent.builder().entityType(POI_ENTITY_TYPE).entityClassifier("whatever").build();
        Assert.assertTrue(new CrudEventMatcher(filter()).matches(event));
    }


    @Test
    public void eventInAdminZoneMatching() {

        AdministrativeZone zone = adminZone();
        CrudEventFilter filterWithAdminZone = filter();
        filterWithAdminZone.getAdministrativeZones().add(zone);


        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).geometry(zone.getPolygon().getCentroid()).build();
        Assert.assertTrue(new CrudEventMatcher(filterWithAdminZone).matches(event));
    }

    @Test
    public void eventOutsideAdminZoneNotMatching() {
        AdministrativeZone zone = adminZone();
        CrudEventFilter filterWithAdminZone = filter();
        filterWithAdminZone.getAdministrativeZones().add(zone);

        Point pointOutside=new GeometryFactory().createPoint(new Coordinate(-50,-50));

        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).geometry(pointOutside).build();
        Assert.assertFalse(new CrudEventMatcher(filterWithAdminZone).matches(event));
    }

    @Test
    public void eventWrongTypeNotMatchingFilter() {
        CrudEvent event = CrudEvent.builder().entityType("NotMatchingType").entityClassifier(BUS_ENTITY_CLASSIFICATION).build();
        Assert.assertFalse(new CrudEventMatcher(filter()).matches(event));
    }

    @Test
    public void eventWrongClassificationNotMatchingFilter() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier("onstreetTram").build();
        Assert.assertFalse(new CrudEventMatcher(filter()).matches(event));
    }

    private CrudEventFilter filter() {
        CrudEventFilter filter = new CrudEventFilter();

        EntityType stopType = new EntityType();
        stopType.setPrivateCode(STOP_ENTITY_TYPE);
        EntityClassification busStopClassification = new EntityClassification();
        busStopClassification.setPrivateCode(BUS_ENTITY_CLASSIFICATION);
        busStopClassification.setEntityType(stopType);

        EntityType poiType = new EntityType();
        poiType.setPrivateCode(POI_ENTITY_TYPE);
        EntityClassification allPois = new EntityClassification();
        allPois.setEntityType(poiType);
        allPois.setPrivateCode(EntityClassification.ALL_TYPES);

        filter.setEntityClassifications(Sets.newHashSet(allPois, busStopClassification));
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
