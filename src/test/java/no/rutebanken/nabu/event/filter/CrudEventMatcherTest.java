package no.rutebanken.nabu.event.filter;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.event.user.AdministrativeZoneRepository;
import no.rutebanken.nabu.event.user.dto.TypeDTO;
import no.rutebanken.nabu.event.user.dto.responsibility.EntityClassificationDTO;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import no.rutebanken.nabu.event.user.model.AdministrativeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CrudEventMatcherTest {

    private static final String STOP_ENTITY_TYPE = "StopPlace";

    private static final String BUS_ENTITY_CLASSIFICATION = "onstreetBus";

    private AdministrativeZoneRepository administrativeZoneRepositoryMock;

    @Before
    public void setUp() throws Exception {
        administrativeZoneRepositoryMock = mock(AdministrativeZoneRepository.class);
    }

    @Test
    public void eventMatchingFilterWithoutAdminZonesSpecificType() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).build();
        Assert.assertTrue(new CrudEventMatcher(administrativeZoneRepositoryMock, filter(BUS_ENTITY_CLASSIFICATION)).matches(event));
    }

    @Test
    public void eventMatchingFilterWithoutAdminZonesWildcardType() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier("whatever").build();
        Assert.assertTrue(new CrudEventMatcher(administrativeZoneRepositoryMock, filter(EventMatcher.ALL_TYPES)).matches(event));
    }


    @Test
    public void eventInAdminZoneMatching() {
        AdministrativeZone zone = adminZone();
        EventFilterDTO filterWithAdminZone = filter(BUS_ENTITY_CLASSIFICATION);
        filterWithAdminZone.getAdministrativeZoneRefs().add(zone.getId());

        when(administrativeZoneRepositoryMock.getAdministrativeZone(zone.getId())).thenReturn(zone);

        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).geometry(zone.getPolygon().getCentroid()).build();
        Assert.assertTrue(new CrudEventMatcher(administrativeZoneRepositoryMock, filterWithAdminZone).matches(event));
    }

    @Test
    public void eventOutsideAdminZoneNotMatching() {
        AdministrativeZone zone = adminZone();
        EventFilterDTO filterWithAdminZone = filter(BUS_ENTITY_CLASSIFICATION);
        filterWithAdminZone.getAdministrativeZoneRefs().add(zone.getId());

        when(administrativeZoneRepositoryMock.getAdministrativeZone(zone.getId())).thenReturn(zone);

        Point pointOutside = new GeometryFactory().createPoint(new Coordinate(-50, -50));

        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier(BUS_ENTITY_CLASSIFICATION).geometry(pointOutside).build();
        Assert.assertFalse(new CrudEventMatcher(administrativeZoneRepositoryMock, filterWithAdminZone).matches(event));
    }

    @Test
    public void eventWrongTypeNotMatchingFilter() {
        CrudEvent event = CrudEvent.builder().entityType("NotMatchingType").entityClassifier(BUS_ENTITY_CLASSIFICATION).build();
        Assert.assertFalse(new CrudEventMatcher(administrativeZoneRepositoryMock, filter(EventMatcher.ALL_TYPES)).matches(event));
    }

    @Test
    public void eventWrongClassificationNotMatchingFilter() {
        CrudEvent event = CrudEvent.builder().entityType(STOP_ENTITY_TYPE).entityClassifier("onstreetTram").build();
        Assert.assertFalse(new CrudEventMatcher(administrativeZoneRepositoryMock, filter(BUS_ENTITY_CLASSIFICATION)).matches(event));
    }

    private EventFilterDTO filter(String stopPlaceTypeClassificationCode) {
        EntityClassificationDTO entityTypeStopPlaceClassification = new EntityClassificationDTO();
        entityTypeStopPlaceClassification.privateCode = STOP_ENTITY_TYPE;
        entityTypeStopPlaceClassification.entityType = new TypeDTO();
        entityTypeStopPlaceClassification.entityType.privateCode = "EntityType";


        EntityClassificationDTO stopPlaceTypeClassification = new EntityClassificationDTO();
        stopPlaceTypeClassification.privateCode = stopPlaceTypeClassificationCode;
        stopPlaceTypeClassification.entityType = new TypeDTO();
        stopPlaceTypeClassification.entityType.privateCode = "StopPlaceType";

        EventFilterDTO eventFilter = new EventFilterDTO(EventFilterDTO.EventFilterType.CRUD);
        eventFilter.entityClassifications.add(entityTypeStopPlaceClassification);
        eventFilter.entityClassifications.add(stopPlaceTypeClassification);
        return eventFilter;
    }


    private AdministrativeZone adminZone() {

        GeometryFactory fact = new GeometryFactory();
        LinearRing linear = new GeometryFactory().createLinearRing(new Coordinate[]{new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)});
        return new AdministrativeZone("test", "name", new Polygon(linear, null, fact));
    }
}
