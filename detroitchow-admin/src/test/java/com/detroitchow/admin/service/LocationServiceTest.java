package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.repository.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("LocationService Tests")
class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("Should create a location with auto-generated ID")
    void testCreateLocation() {
        // Given
        Location locationToCreate = Location.builder()
                .name("New Restaurant")
                .description("A new restaurant")
                .operatingStatus("active")
                .address1("456 Oak Ave")
                .city("Detroit")
                .region("Michigan")
                .country("USA")
                .build();

        // When
        Location created = locationService.createLocation(locationToCreate);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getLocationid()).isNotNull();
        assertThat(created.getLocationid()).startsWith("loc_");
        assertThat(created.getName()).isEqualTo("New Restaurant");
        assertThat(created.getDescription()).isEqualTo("A new restaurant");
        assertThat(created.getOperatingStatus()).isEqualTo("active");
        assertThat(created.getCreateDate()).isNotNull();
        assertThat(created.getUpdatedDate()).isNotNull();

        // Verify it was saved to database
        Optional<Location> savedLocation = locationRepository.findById(created.getLocationid());
        assertThat(savedLocation).isPresent();
        assertThat(savedLocation.get().getName()).isEqualTo("New Restaurant");
    }

    @Test
    @DisplayName("Should get location by ID successfully")
    void testGetLocationById_Found() {
        // Given: loc-001 exists in test data

        // When
        Optional<Location> result = locationService.getLocationById("loc-001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getLocationid()).isEqualTo("loc-001");
        assertThat(result.get().getName()).isEqualTo("Lafayette Coney Island");
        assertThat(result.get().getCity()).isEqualTo("Detroit");
        assertThat(result.get().getOperatingStatus()).isEqualTo("active");
    }

    @Test
    @DisplayName("Should return empty optional when location not found")
    void testGetLocationById_NotFound() {
        // When
        Optional<Location> result = locationService.getLocationById("nonexistent-id");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update location successfully")
    void testUpdateLocation_Success() {
        // Given: loc-001 exists in test data
        Location existingLocation = locationRepository.findById("loc-001").orElseThrow();

        Location updatedLocation = Location.builder()
                .locationid("loc-001")
                .name("Updated Lafayette Coney Island")
                .description("Updated description")
                .operatingStatus("active")
                .address1("999 Updated St")
                .city("Detroit")
                .region("Michigan")
                .country("USA")
                .phone1("313-999-9999")
                .build();

        // When
        Location result = locationService.updateLocation(updatedLocation);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Lafayette Coney Island");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getAddress1()).isEqualTo("999 Updated St");
        assertThat(result.getPhone1()).isEqualTo("313-999-9999");
        assertThat(result.getUpdatedDate()).isNotNull();
        assertThat(result.getUpdatedDate()).isAfterOrEqualTo(existingLocation.getUpdatedDate());

        // Verify in database
        Location fromDb = locationRepository.findById("loc-001").orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("Updated Lafayette Coney Island");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent location")
    void testUpdateLocation_NotFound() {
        // Given
        Location nonExistentLocation = Location.builder()
                .locationid("nonexistent-id")
                .name("Fake Restaurant")
                .build();

        // When & Then
        assertThatThrownBy(() -> locationService.updateLocation(nonExistentLocation))
                .isInstanceOf(LocationService.LocationNotFoundException.class)
                .hasMessageContaining("Location not found")
                .hasMessageContaining("nonexistent-id");
    }

    @Test
    @DisplayName("Should delete location successfully")
    void testDeleteLocation_Success() {
        // Given: Create a location to delete
        Location toDelete = Location.builder()
                .name("To Be Deleted")
                .city("Detroit")
                .operatingStatus("active")
                .build();
        Location created = locationService.createLocation(toDelete);
        String locationId = created.getLocationid();

        // Verify it exists
        assertThat(locationRepository.existsById(locationId)).isTrue();

        // When
        locationService.deleteLocation(locationId);

        // Then
        assertThat(locationRepository.existsById(locationId)).isFalse();
        assertThat(locationRepository.findById(locationId)).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent location")
    void testDeleteLocation_NotFound() {
        // When & Then
        assertThatThrownBy(() -> locationService.deleteLocation("nonexistent-id"))
                .isInstanceOf(LocationService.LocationNotFoundException.class)
                .hasMessageContaining("Location not found")
                .hasMessageContaining("nonexistent-id");
    }

    @Test
    @DisplayName("Should get all locations")
    void testGetAllLocations() {
        // Given: Test data has 8 locations (loc-001 through loc-008)

        // When
        List<Location> result = locationService.getAllLocations();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSizeGreaterThanOrEqualTo(8);

        // Verify some known locations from test data
        assertThat(result).anyMatch(loc -> loc.getLocationid().equals("loc-001"));
        assertThat(result).anyMatch(loc -> loc.getName().equals("Lafayette Coney Island"));
        assertThat(result).anyMatch(loc -> loc.getName().equals("Buddy's Pizza"));
        assertThat(result).anyMatch(loc -> loc.getOperatingStatus().equals("temporarily_closed")); // loc-008
    }

    @Test
    @DisplayName("Should create location with minimal required fields")
    void testCreateLocation_MinimalFields() {
        // Given
        Location minimalLocation = Location.builder()
                .name("Minimal Restaurant")
                .build();

        // When
        Location created = locationService.createLocation(minimalLocation);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getLocationid()).isNotNull();
        assertThat(created.getLocationid()).startsWith("loc_");
        assertThat(created.getName()).isEqualTo("Minimal Restaurant");
        assertThat(created.getCreateDate()).isNotNull();
        assertThat(created.getUpdatedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should preserve create date when updating location")
    void testUpdateLocation_PreservesCreateDate() {
        // Given: Create a new location
        Location newLocation = Location.builder()
                .name("Original Name")
                .city("Detroit")
                .build();
        Location created = locationService.createLocation(newLocation);
        String locationId = created.getLocationid();

        // Pause briefly to ensure different timestamp
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        // When: Update the location
        Location updateData = Location.builder()
                .locationid(locationId)
                .name("Updated Name")
                .city("Ann Arbor")
                .build();
        Location updated = locationService.updateLocation(updateData);

        // Then
        assertThat(updated.getCreateDate()).isEqualTo(created.getCreateDate());
        assertThat(updated.getUpdatedDate()).isAfterOrEqualTo(created.getUpdatedDate());
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getCity()).isEqualTo("Ann Arbor");
    }

    @Test
    @DisplayName("Should update all location fields")
    void testUpdateLocation_AllFields() {
        // Given: loc-001 exists
        Location fullUpdate = Location.builder()
                .locationid("loc-001")
                .name("Fully Updated Restaurant")
                .description("Completely new description")
                .operatingStatus("temporarily_closed")
                .address1("111 New St")
                .address2("Suite 200")
                .city("Ann Arbor")
                .locality("Downtown")
                .zip("48104")
                .region("MI")
                .country("US")
                .phone1("734-111-1111")
                .phone2("734-222-2222")
                .lat("42.2808")
                .lng("-83.7430")
                .website("https://newsite.com")
                .facebook("https://facebook.com/new")
                .twitter("https://twitter.com/new")
                .instagram("https://instagram.com/new")
                .opentable("https://opentable.com/new")
                .tripadvisor("https://tripadvisor.com/new")
                .yelp("https://yelp.com/new")
                .hours("Mon-Fri: 10am-8pm")
                .contactText("Call for reservations")
                .build();

        // When
        Location updated = locationService.updateLocation(fullUpdate);

        // Then
        assertThat(updated.getName()).isEqualTo("Fully Updated Restaurant");
        assertThat(updated.getDescription()).isEqualTo("Completely new description");
        assertThat(updated.getOperatingStatus()).isEqualTo("temporarily_closed");
        assertThat(updated.getAddress1()).isEqualTo("111 New St");
        assertThat(updated.getAddress2()).isEqualTo("Suite 200");
        assertThat(updated.getCity()).isEqualTo("Ann Arbor");
        assertThat(updated.getLocality()).isEqualTo("Downtown");
        assertThat(updated.getZip()).isEqualTo("48104");
        assertThat(updated.getRegion()).isEqualTo("MI");
        assertThat(updated.getCountry()).isEqualTo("US");
        assertThat(updated.getPhone1()).isEqualTo("734-111-1111");
        assertThat(updated.getPhone2()).isEqualTo("734-222-2222");
        assertThat(updated.getLat()).isEqualTo("42.2808");
        assertThat(updated.getLng()).isEqualTo("-83.7430");
        assertThat(updated.getWebsite()).isEqualTo("https://newsite.com");
        assertThat(updated.getFacebook()).isEqualTo("https://facebook.com/new");
        assertThat(updated.getTwitter()).isEqualTo("https://twitter.com/new");
        assertThat(updated.getInstagram()).isEqualTo("https://instagram.com/new");
        assertThat(updated.getOpentable()).isEqualTo("https://opentable.com/new");
        assertThat(updated.getTripadvisor()).isEqualTo("https://tripadvisor.com/new");
        assertThat(updated.getYelp()).isEqualTo("https://yelp.com/new");
        assertThat(updated.getHours()).isEqualTo("Mon-Fri: 10am-8pm");
        assertThat(updated.getContactText()).isEqualTo("Call for reservations");
    }

    @Test
    @DisplayName("Should get location with specific status from test data")
    void testGetLocationByStatus() {
        // Given: loc-008 has temporarily_closed status

        // When
        Optional<Location> result = locationService.getLocationById("loc-008");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Closed Test Restaurant");
        assertThat(result.get().getOperatingStatus()).isEqualTo("temporarily_closed");
    }

    @Test
    @DisplayName("Should generate unique location IDs")
    void testCreateLocation_GeneratesUniqueIds() {
        // Given
        Location location1 = Location.builder().name("Restaurant 1").build();
        Location location2 = Location.builder().name("Restaurant 2").build();
        Location location3 = Location.builder().name("Restaurant 3").build();

        // When
        Location created1 = locationService.createLocation(location1);
        Location created2 = locationService.createLocation(location2);
        Location created3 = locationService.createLocation(location3);

        // Then
        assertThat(created1.getLocationid()).isNotEqualTo(created2.getLocationid());
        assertThat(created1.getLocationid()).isNotEqualTo(created3.getLocationid());
        assertThat(created2.getLocationid()).isNotEqualTo(created3.getLocationid());

        // All should start with loc_
        assertThat(created1.getLocationid()).startsWith("loc_");
        assertThat(created2.getLocationid()).startsWith("loc_");
        assertThat(created3.getLocationid()).startsWith("loc_");
    }
}
