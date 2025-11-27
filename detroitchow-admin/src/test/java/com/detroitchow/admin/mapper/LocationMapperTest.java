package com.detroitchow.admin.mapper;

import com.detroitchow.admin.dto.GooglePlacesDto;
import com.detroitchow.admin.dto.LocationDto;
import com.detroitchow.admin.dto.MenuDto;
import com.detroitchow.admin.entity.GooglePlaces;
import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.entity.Menu;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocationMapperTest {

    private LocationMapper locationMapper;
    private MenuMapper menuMapper;
    private GooglePlacesMapper googlePlacesMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        menuMapper = new MenuMapper();
        googlePlacesMapper = new GooglePlacesMapper();
        locationMapper = new LocationMapper(menuMapper, googlePlacesMapper);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testToDto_WithFullLocation_ShouldMapAllFields() {
        // Given
        OffsetDateTime createDate = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedDate = OffsetDateTime.now();

        Location location = Location.builder()
                .locationid("loc-123")
                .name("Test Restaurant")
                .description("A great place to eat")
                .status(Location.LocationStatus.active)
                .address1("123 Main St")
                .address2("Suite 100")
                .city("Detroit")
                .locality("Downtown")
                .zip("48201")
                .region("MI")
                .country("US")
                .phone1("313-555-1234")
                .phone2("313-555-5678")
                .lat("42.3314")
                .lng("-83.0458")
                .website("https://example.com")
                .facebook("https://facebook.com/restaurant")
                .twitter("https://twitter.com/restaurant")
                .instagram("https://instagram.com/restaurant")
                .opentable("https://opentable.com/restaurant")
                .tripadvisor("https://tripadvisor.com/restaurant")
                .yelp("https://yelp.com/restaurant")
                .hours("Mon-Fri: 9am-5pm")
                .contactText("Contact us for reservations")
                .createDate(createDate)
                .createUser("admin")
                .updatedDate(updatedDate)
                .updateUser("admin2")
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertEquals("loc-123", dto.getLocationid());
        assertEquals("Test Restaurant", dto.getName());
        assertEquals("A great place to eat", dto.getDescription());
        assertEquals("active", dto.getStatus());
        assertEquals("123 Main St", dto.getAddress1());
        assertEquals("Suite 100", dto.getAddress2());
        assertEquals("Detroit", dto.getCity());
        assertEquals("Downtown", dto.getLocality());
        assertEquals("48201", dto.getZip());
        assertEquals("MI", dto.getRegion());
        assertEquals("US", dto.getCountry());
        assertEquals("313-555-1234", dto.getPhone1());
        assertEquals("313-555-5678", dto.getPhone2());
        assertEquals(42.3314, dto.getLat());
        assertEquals(-83.0458, dto.getLng());
        assertEquals("https://example.com", dto.getWebsite());
        assertEquals("https://facebook.com/restaurant", dto.getFacebook());
        assertEquals("https://twitter.com/restaurant", dto.getTwitter());
        assertEquals("https://instagram.com/restaurant", dto.getInstagram());
        assertEquals("https://opentable.com/restaurant", dto.getOpentable());
        assertEquals("https://tripadvisor.com/restaurant", dto.getTripadvisor());
        assertEquals("https://yelp.com/restaurant", dto.getYelp());
        assertEquals("Mon-Fri: 9am-5pm", dto.getHours());
        assertEquals("Contact us for reservations", dto.getContactText());
        assertEquals(createDate, dto.getCreateDate());
        assertEquals("admin", dto.getCreateUser());
        assertEquals(updatedDate, dto.getUpdatedDate());
        assertEquals("admin2", dto.getUpdateUser());
    }

    @Test
    void testToDto_WithNullLocation_ShouldReturnNull() {
        // When
        LocationDto dto = locationMapper.toDto(null);

        // Then
        assertNull(dto);
    }

    @Test
    void testToDto_WithNullStatus_ShouldMapStatusAsNull() {
        // Given
        Location location = Location.builder()
                .locationid("loc-456")
                .name("Restaurant Without Status")
                .status(null)
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertNull(dto.getStatus());
    }

    @Test
    void testToDto_WithNullLatLng_ShouldMapAsNull() {
        // Given
        Location location = Location.builder()
                .locationid("loc-789")
                .name("Restaurant Without Coordinates")
                .lat(null)
                .lng(null)
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertNull(dto.getLat());
        assertNull(dto.getLng());
    }

    @Test
    void testToDto_WithValidLatLng_ShouldParseToDouble() {
        // Given
        Location location = Location.builder()
                .locationid("loc-coordinates")
                .name("Restaurant With Coordinates")
                .lat("42.5000")
                .lng("-83.1000")
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertEquals(42.5000, dto.getLat());
        assertEquals(-83.1000, dto.getLng());
    }

    @Test
    void testToDto_WithMenus_ShouldMapMenus() {
        // Given
        Menu menu1 = Menu.builder()
                .menuLink("https://menu1.pdf")
                .descr("Dinner Menu")
                .priority(1)
                .build();
        Menu menu2 = Menu.builder()
                .menuLink("https://menu2.pdf")
                .descr("Lunch Menu")
                .priority(2)
                .build();

        Location location = Location.builder()
                .locationid("loc-with-menus")
                .name("Restaurant With Menus")
                .menus(Arrays.asList(menu1, menu2))
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.getMenus());
        assertEquals(2, dto.getMenus().size());
        assertEquals("https://menu1.pdf", dto.getMenus().get(0).getMenuLink());
        assertEquals("Dinner Menu", dto.getMenus().get(0).getDescr());
        assertEquals("https://menu2.pdf", dto.getMenus().get(1).getMenuLink());
        assertEquals("Lunch Menu", dto.getMenus().get(1).getDescr());
    }

    @Test
    void testToDto_WithNullMenus_ShouldMapAsNull() {
        // Given
        Location location = Location.builder()
                .locationid("loc-no-menus")
                .name("Restaurant Without Menus")
                .menus(null)
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertNull(dto.getMenus());
    }

    @Test
    void testToDto_WithGooglePlaces_ShouldMapGooglePlaces() throws Exception {
        // Given
        JsonNode txtsearchJson = objectMapper.readTree("{\"results\":[]}");

        GooglePlaces googlePlaces = GooglePlaces.builder()
                .id(123)
                .placeId("ChIJTest123")
                .lat("42.3314")
                .lng("-83.0458")
                .formattedAddress("123 Test St, Detroit, MI")
                .txtsearchJson(txtsearchJson)
                .build();

        Location location = Location.builder()
                .locationid("loc-with-google")
                .name("Restaurant With Google Places")
                .googlePlaces(googlePlaces)
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.getGooglePlaces());
        assertEquals(123, dto.getGooglePlaces().getId());
        assertEquals("ChIJTest123", dto.getGooglePlaces().getPlaceId());
        assertEquals("42.3314", dto.getGooglePlaces().getLat());
        assertEquals("-83.0458", dto.getGooglePlaces().getLng());
        assertEquals("123 Test St, Detroit, MI", dto.getGooglePlaces().getFormattedAddress());
    }

    @Test
    void testToDto_WithNullGooglePlaces_ShouldMapAsNull() {
        // Given
        Location location = Location.builder()
                .locationid("loc-no-google")
                .name("Restaurant Without Google Places")
                .googlePlaces(null)
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertNull(dto.getGooglePlaces());
    }

    @Test
    void testToDto_WithAllLocationStatuses_ShouldMapCorrectly() {
        // Test active status
        Location activeLocation = Location.builder()
                .locationid("loc-active")
                .name("Active Restaurant")
                .status(Location.LocationStatus.active)
                .build();

        LocationDto activeDto = locationMapper.toDto(activeLocation);
        assertEquals("active", activeDto.getStatus());

        // Test temporarily_closed status
        Location tempClosedLocation = Location.builder()
                .locationid("loc-temp-closed")
                .name("Temporarily Closed Restaurant")
                .status(Location.LocationStatus.temporarily_closed)
                .build();

        LocationDto tempClosedDto = locationMapper.toDto(tempClosedLocation);
        assertEquals("temporarily_closed", tempClosedDto.getStatus());

        // Test permanently_closed status
        Location permClosedLocation = Location.builder()
                .locationid("loc-perm-closed")
                .name("Permanently Closed Restaurant")
                .status(Location.LocationStatus.permanently_closed)
                .build();

        LocationDto permClosedDto = locationMapper.toDto(permClosedLocation);
        assertEquals("permanently_closed", permClosedDto.getStatus());
    }

    @Test
    void testToDto_WithMinimalLocation_ShouldMapOnlyProvidedFields() {
        // Given
        Location location = Location.builder()
                .locationid("loc-minimal")
                .name("Minimal Restaurant")
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertEquals("loc-minimal", dto.getLocationid());
        assertEquals("Minimal Restaurant", dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getStatus());
        assertNull(dto.getAddress1());
        assertNull(dto.getCity());
        assertNull(dto.getLat());
        assertNull(dto.getLng());
    }

    @Test
    void testToEntity_WithValidStatus_ShouldParseStatusEnum() {
        // Given
        LocationDto dto = LocationDto.builder()
                .locationid("loc-entity-test")
                .name("Entity Test Restaurant")
                .status("active")
                .build();

        // When
        Location location = locationMapper.toEntity(dto);

        // Then
        assertNotNull(location);
        assertEquals("loc-entity-test", location.getLocationid());
        assertEquals("Entity Test Restaurant", location.getName());
    }

    @Test
    void testToEntity_WithInvalidStatus_ShouldDefaultToActive() {
        // Given
        LocationDto dto = LocationDto.builder()
                .locationid("loc-invalid-status")
                .name("Invalid Status Restaurant")
                .status("invalid_status")
                .build();

        // When
        Location location = locationMapper.toEntity(dto);

        // Then
        assertNotNull(location);
        assertEquals("loc-invalid-status", location.getLocationid());
    }

    @Test
    void testToEntity_WithNullStatus_ShouldHandleGracefully() {
        // Given
        LocationDto dto = LocationDto.builder()
                .locationid("loc-null-status")
                .name("Null Status Restaurant")
                .status(null)
                .build();

        // When
        Location location = locationMapper.toEntity(dto);

        // Then
        assertNotNull(location);
        assertEquals("loc-null-status", location.getLocationid());
        assertEquals("Null Status Restaurant", location.getName());
    }

    @Test
    void testToEntity_WithNullDto_ShouldReturnNull() {
        // When
        Location location = locationMapper.toEntity(null);

        // Then
        assertNull(location);
    }

    @Test
    void testToDto_WithComplexScenario_ShouldMapAllRelationships() throws Exception {
        // Given
        OffsetDateTime now = OffsetDateTime.now();

        Menu menu = Menu.builder()
                .menuLink("https://dinner-menu.pdf")
                .descr("Dinner")
                .priority(1)
                .build();

        JsonNode txtsearchJson = objectMapper.readTree("{\"results\":[]}");

        GooglePlaces googlePlaces = GooglePlaces.builder()
                .id(999)
                .placeId("ChIJComplexTest")
                .lat("42.3000")
                .lng("-83.0500")
                .txtsearchJson(txtsearchJson)
                .build();

        Location location = Location.builder()
                .locationid("osm-n123456")
                .name("Complex Test Restaurant")
                .description("Testing all relationships")
                .status(Location.LocationStatus.active)
                .address1("100 Complex Ave")
                .city("Detroit")
                .region("MI")
                .country("US")
                .lat("42.3000")
                .lng("-83.0500")
                .website("https://complex.com")
                .createDate(now)
                .createUser("system")
                .menus(Arrays.asList(menu))
                .googlePlaces(googlePlaces)
                .build();

        // When
        LocationDto dto = locationMapper.toDto(location);

        // Then
        assertNotNull(dto);
        assertEquals("osm-n123456", dto.getLocationid());
        assertEquals("Complex Test Restaurant", dto.getName());
        assertEquals("Testing all relationships", dto.getDescription());
        assertEquals("active", dto.getStatus());
        assertEquals(42.3000, dto.getLat());
        assertEquals(-83.0500, dto.getLng());

        assertNotNull(dto.getMenus());
        assertEquals(1, dto.getMenus().size());
        assertEquals("https://dinner-menu.pdf", dto.getMenus().get(0).getMenuLink());

        assertNotNull(dto.getGooglePlaces());
        assertEquals(999, dto.getGooglePlaces().getId());
        assertEquals("ChIJComplexTest", dto.getGooglePlaces().getPlaceId());
    }
}
