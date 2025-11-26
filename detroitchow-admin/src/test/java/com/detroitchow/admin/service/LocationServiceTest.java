package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.entity.Location.LocationStatus;
import com.detroitchow.admin.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationService Tests")
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;
    private String testLocationId;

    @BeforeEach
    void setUp() {
        testLocationId = "loc_" + UUID.randomUUID().toString().substring(0, 8);
        testLocation = Location.builder()
                .locationid(testLocationId)
                .name("Test Restaurant")
                .description("A test restaurant")
                .status(LocationStatus.active)
                .address1("123 Main St")
                .city("Detroit")
                .region("Michigan")
                .country("USA")
                .createDate(OffsetDateTime.now())
                .createUser("test_user")
                .build();
    }

    @Test
    @DisplayName("Should create a location with auto-generated ID")
    void testCreateLocation() {
        // Arrange
        Location locationToCreate = Location.builder()
                .name("New Restaurant")
                .description("A new restaurant")
                .status(LocationStatus.active)
                .address1("456 Oak Ave")
                .city("Detroit")
                .region("Michigan")
                .country("USA")
                .build();

        Location savedLocation = Location.builder()
                .locationid("loc_12345678")
                .name(locationToCreate.getName())
                .description(locationToCreate.getDescription())
                .status(locationToCreate.getStatus())
                .address1(locationToCreate.getAddress1())
                .city(locationToCreate.getCity())
                .region(locationToCreate.getRegion())
                .country(locationToCreate.getCountry())
                .createDate(OffsetDateTime.now())
                .createUser("system")
                .updatedDate(OffsetDateTime.now())
                .build();

        when(locationRepository.save(any(Location.class))).thenReturn(savedLocation);

        // Act
        Location created = locationService.createLocation(locationToCreate);

        // Assert
        assertThat(created).isNotNull();
        assertThat(created.getLocationid()).isNotNull();
        assertThat(created.getLocationid()).startsWith("loc_");
        assertThat(created.getName()).isEqualTo("New Restaurant");
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    @DisplayName("Should get location by ID successfully")
    void testGetLocationById_Found() {
        // Arrange
        when(locationRepository.findById(testLocationId)).thenReturn(Optional.of(testLocation));

        // Act
        Optional<Location> result = locationService.getLocationById(testLocationId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getLocationid()).isEqualTo(testLocationId);
        assertThat(result.get().getName()).isEqualTo("Test Restaurant");
        verify(locationRepository, times(1)).findById(testLocationId);
    }

    @Test
    @DisplayName("Should return empty optional when location not found")
    void testGetLocationById_NotFound() {
        // Arrange
        when(locationRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<Location> result = locationService.getLocationById("nonexistent");

        // Assert
        assertThat(result).isEmpty();
        verify(locationRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("Should update location successfully")
    void testUpdateLocation_Success() {
        // Arrange
        Location updatedLocation = Location.builder()
                .locationid(testLocationId)
                .name("Updated Restaurant")
                .description("Updated description")
                .status(LocationStatus.active)
                .address1("789 Pine Rd")
                .city("Detroit")
                .region("Michigan")
                .country("USA")
                .createDate(testLocation.getCreateDate())
                .createUser(testLocation.getCreateUser())
                .build();

        Location savedResult = Location.builder()
                .locationid(testLocationId)
                .name("Updated Restaurant")
                .description("Updated description")
                .status(LocationStatus.active)
                .address1("789 Pine Rd")
                .city("Detroit")
                .region("Michigan")
                .country("USA")
                .createDate(testLocation.getCreateDate())
                .createUser(testLocation.getCreateUser())
                .updatedDate(OffsetDateTime.now())
                .build();

        when(locationRepository.findById(testLocationId)).thenReturn(Optional.of(testLocation));
        when(locationRepository.save(any(Location.class))).thenReturn(savedResult);

        // Act
        Location result = locationService.updateLocation(updatedLocation);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Restaurant");
        verify(locationRepository, times(1)).findById(testLocationId);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent location")
    void testUpdateLocation_NotFound() {
        // Arrange
        Location nonExistentLocation = Location.builder()
                .locationid("nonexistent")
                .name("Fake Restaurant")
                .build();

        when(locationRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> locationService.updateLocation(nonExistentLocation))
                .isInstanceOf(LocationService.LocationNotFoundException.class)
                .hasMessageContaining("Location not found");
        verify(locationRepository, times(1)).findById("nonexistent");
    }

    @Test
    @DisplayName("Should delete location successfully")
    void testDeleteLocation_Success() {
        // Arrange
        when(locationRepository.existsById(testLocationId)).thenReturn(true);
        doNothing().when(locationRepository).deleteById(testLocationId);

        // Act
        locationService.deleteLocation(testLocationId);

        // Assert
        verify(locationRepository, times(1)).existsById(testLocationId);
        verify(locationRepository, times(1)).deleteById(testLocationId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent location")
    void testDeleteLocation_NotFound() {
        // Arrange
        when(locationRepository.existsById("nonexistent")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> locationService.deleteLocation("nonexistent"))
                .isInstanceOf(LocationService.LocationNotFoundException.class)
                .hasMessageContaining("Location not found");
        verify(locationRepository, times(1)).existsById("nonexistent");
    }

    @Test
    @DisplayName("Should get all locations with pagination")
    void testGetAllLocations_WithPagination() {
        // Arrange
        List<Location> locations = new ArrayList<>();
        locations.add(testLocation);
        locations.add(Location.builder()
                .locationid("loc_test2")
                .name("Second Restaurant")
                .city("Detroit")
                .status(LocationStatus.active)
                .build());

        // Mock findAll(Pageable) - method signature: getAllLocations(String status, int limit, int offset)
        when(locationRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(locations));

        // Act
        var result = locationService.getAllLocations(null, 10, 0);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(locationRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should get locations filtered by status")
    void testGetAllLocations_FilterByStatus() {
        // Arrange
        List<Location> activeLocations = new ArrayList<>();
        activeLocations.add(testLocation);

        when(locationRepository.findByStatus(eq(LocationStatus.active), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(activeLocations));

        // Act
        var result = locationService.getAllLocations("active", 10, 0);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(LocationStatus.active);
        verify(locationRepository, times(1)).findByStatus(eq(LocationStatus.active), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should get locations by city")
    void testGetLocationsByCity() {
        // Arrange
        List<Location> detroitLocations = new ArrayList<>();
        detroitLocations.add(testLocation);

        when(locationRepository.findByCity("Detroit")).thenReturn(detroitLocations);

        // Act
        List<Location> result = locationService.getLocationsByCity("Detroit");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Detroit");
        verify(locationRepository, times(1)).findByCity("Detroit");
    }

    @Test
    @DisplayName("Should get locations by region")
    void testGetLocationsByRegion() {
        // Arrange
        List<Location> michiganLocations = new ArrayList<>();
        michiganLocations.add(testLocation);

        when(locationRepository.findByRegion("Michigan")).thenReturn(michiganLocations);

        // Act
        List<Location> result = locationService.getLocationsByRegion("Michigan");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRegion()).isEqualTo("Michigan");
        verify(locationRepository, times(1)).findByRegion("Michigan");
    }
}