package com.detroitchow.admin.controller;

import com.detroitchow.admin.dto.LocationDto;
import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.entity.Location.LocationStatus;
import com.detroitchow.admin.mapper.LocationMapper;
import com.detroitchow.admin.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationsController.class)
class LocationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService locationService;

    @MockBean
    private LocationMapper locationMapper;

    private Location testLocation;
    private LocationDto testLocationDto;

    @BeforeEach
    void setUp() {
        OffsetDateTime now = OffsetDateTime.now();

        testLocation = Location.builder()
                .locationid("loc-001")
                .name("Test Restaurant")
                .description("A test restaurant")
                .status(LocationStatus.active)
                .address1("123 Test St")
                .city("Detroit")
                .region("Michigan")
                .country("US")
                .phone1("313-555-0100")
                .lat("42.3314")
                .lng("-83.0458")
                .website("https://testrestaurant.com")
                .createDate(now)
                .updatedDate(now)
                .build();

        testLocationDto = LocationDto.builder()
                .locationid("loc-001")
                .name("Test Restaurant")
                .description("A test restaurant")
                .status("active")
                .address1("123 Test St")
                .city("Detroit")
                .region("Michigan")
                .country("US")
                .phone1("313-555-0100")
                .lat(42.3314)
                .lng(-83.0458)
                .website("https://testrestaurant.com")
                .createDate(now)
                .updatedDate(now)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/locations - Should return all locations")
    void testGetAllLocations() throws Exception {
        // Given
        Location location2 = Location.builder()
                .locationid("loc-002")
                .name("Another Restaurant")
                .city("Ann Arbor")
                .status(LocationStatus.active)
                .build();

        LocationDto locationDto2 = LocationDto.builder()
                .locationid("loc-002")
                .name("Another Restaurant")
                .city("Ann Arbor")
                .status("active")
                .build();

        List<Location> locations = Arrays.asList(testLocation, location2);

        when(locationService.getAllLocations()).thenReturn(locations);
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDto);
        when(locationMapper.toDto(location2)).thenReturn(locationDto2);

        // When & Then
        mockMvc.perform(get("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].locationid").value("loc-001"))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"))
                .andExpect(jsonPath("$[0].city").value("Detroit"))
                .andExpect(jsonPath("$[1].locationid").value("loc-002"))
                .andExpect(jsonPath("$[1].name").value("Another Restaurant"))
                .andExpect(jsonPath("$[1].city").value("Ann Arbor"));

        verify(locationService, times(1)).getAllLocations();
        verify(locationMapper, times(2)).toDto(any(Location.class));
    }

    @Test
    @DisplayName("GET /api/v1/locations - Should return empty list when no locations")
    void testGetAllLocations_Empty() throws Exception {
        // Given
        when(locationService.getAllLocations()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(locationService, times(1)).getAllLocations();
    }

    @Test
    @DisplayName("GET /api/v1/location/{id} - Should return location when found")
    void testGetLocationById_Found() throws Exception {
        // Given
        when(locationService.getLocationById("loc-001")).thenReturn(Optional.of(testLocation));
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDto);

        // When & Then
        mockMvc.perform(get("/api/v1/location/loc-001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.locationid").value("loc-001"))
                .andExpect(jsonPath("$.data.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.data.description").value("A test restaurant"))
                .andExpect(jsonPath("$.data.city").value("Detroit"))
                .andExpect(jsonPath("$.data.status").value("active"))
                .andExpect(jsonPath("$.data.phone1").value("313-555-0100"))
                .andExpect(jsonPath("$.data.lat").value(42.3314))
                .andExpect(jsonPath("$.data.lng").value(-83.0458));

        verify(locationService, times(1)).getLocationById("loc-001");
        verify(locationMapper, times(1)).toDto(testLocation);
    }

    @Test
    @DisplayName("GET /api/v1/location/{id} - Should return 404 when location not found")
    void testGetLocationById_NotFound() throws Exception {
        // Given
        when(locationService.getLocationById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/location/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(locationService, times(1)).getLocationById("nonexistent");
        verify(locationMapper, never()).toDto(any(Location.class));
    }

    @Test
    @DisplayName("POST /api/v1/location - Should create location successfully")
    void testCreateLocation_Success() throws Exception {
        // Given
        LocationDto newLocationDto = LocationDto.builder()
                .name("New Restaurant")
                .description("A brand new restaurant")
                .status("active")
                .address1("456 New St")
                .city("Detroit")
                .build();

        Location newLocation = Location.builder()
                .name("New Restaurant")
                .description("A brand new restaurant")
                .status(LocationStatus.active)
                .address1("456 New St")
                .city("Detroit")
                .build();

        Location savedLocation = Location.builder()
                .locationid("loc_abc123")
                .name("New Restaurant")
                .description("A brand new restaurant")
                .status(LocationStatus.active)
                .address1("456 New St")
                .city("Detroit")
                .createDate(OffsetDateTime.now())
                .updatedDate(OffsetDateTime.now())
                .build();

        LocationDto savedLocationDto = LocationDto.builder()
                .locationid("loc_abc123")
                .name("New Restaurant")
                .description("A brand new restaurant")
                .status("active")
                .address1("456 New St")
                .city("Detroit")
                .build();

        when(locationMapper.toEntity(any(LocationDto.class))).thenReturn(newLocation);
        when(locationService.createLocation(any(Location.class))).thenReturn(savedLocation);
        when(locationMapper.toDto(savedLocation)).thenReturn(savedLocationDto);

        // When & Then
        mockMvc.perform(post("/api/v1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocationDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.locationid").value("loc_abc123"))
                .andExpect(jsonPath("$.data.name").value("New Restaurant"))
                .andExpect(jsonPath("$.data.description").value("A brand new restaurant"))
                .andExpect(jsonPath("$.data.city").value("Detroit"))
                .andExpect(jsonPath("$.message").value("Location created successfully"));

        verify(locationMapper, times(1)).toEntity(any(LocationDto.class));
        verify(locationService, times(1)).createLocation(any(Location.class));
        verify(locationMapper, times(1)).toDto(savedLocation);
    }

    @Test
    @DisplayName("POST /api/v1/location - Should create location with minimal fields")
    void testCreateLocation_MinimalFields() throws Exception {
        // Given
        LocationDto minimalDto = LocationDto.builder()
                .name("Minimal Restaurant")
                .build();

        Location minimalLocation = Location.builder()
                .name("Minimal Restaurant")
                .build();

        Location savedLocation = Location.builder()
                .locationid("loc_xyz789")
                .name("Minimal Restaurant")
                .createDate(OffsetDateTime.now())
                .updatedDate(OffsetDateTime.now())
                .build();

        LocationDto savedDto = LocationDto.builder()
                .locationid("loc_xyz789")
                .name("Minimal Restaurant")
                .build();

        when(locationMapper.toEntity(any(LocationDto.class))).thenReturn(minimalLocation);
        when(locationService.createLocation(any(Location.class))).thenReturn(savedLocation);
        when(locationMapper.toDto(savedLocation)).thenReturn(savedDto);

        // When & Then
        mockMvc.perform(post("/api/v1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.locationid").value("loc_xyz789"))
                .andExpect(jsonPath("$.data.name").value("Minimal Restaurant"))
                .andExpect(jsonPath("$.message").value("Location created successfully"));

        verify(locationService, times(1)).createLocation(any(Location.class));
    }

    @Test
    @DisplayName("PUT /api/v1/location - Should update location successfully")
    void testUpdateLocation_Success() throws Exception {
        // Given
        LocationDto updateDto = LocationDto.builder()
                .locationid("loc-001")
                .name("Updated Restaurant")
                .description("Updated description")
                .status("active")
                .city("Ann Arbor")
                .build();

        Location updateLocation = Location.builder()
                .locationid("loc-001")
                .name("Updated Restaurant")
                .description("Updated description")
                .status(LocationStatus.active)
                .city("Ann Arbor")
                .build();

        Location updatedLocation = Location.builder()
                .locationid("loc-001")
                .name("Updated Restaurant")
                .description("Updated description")
                .status(LocationStatus.active)
                .city("Ann Arbor")
                .updatedDate(OffsetDateTime.now())
                .build();

        LocationDto updatedDto = LocationDto.builder()
                .locationid("loc-001")
                .name("Updated Restaurant")
                .description("Updated description")
                .status("active")
                .city("Ann Arbor")
                .build();

        when(locationMapper.toEntity(any(LocationDto.class))).thenReturn(updateLocation);
        when(locationService.updateLocation(any(Location.class))).thenReturn(updatedLocation);
        when(locationMapper.toDto(updatedLocation)).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.locationid").value("loc-001"))
                .andExpect(jsonPath("$.data.name").value("Updated Restaurant"))
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.city").value("Ann Arbor"))
                .andExpect(jsonPath("$.message").value("Location updated successfully"));

        verify(locationMapper, times(1)).toEntity(any(LocationDto.class));
        verify(locationService, times(1)).updateLocation(any(Location.class));
        verify(locationMapper, times(1)).toDto(updatedLocation);
    }

    @Test
    @DisplayName("PUT /api/v1/location - Should return 404 when location not found")
    void testUpdateLocation_NotFound() throws Exception {
        // Given
        LocationDto updateDto = LocationDto.builder()
                .locationid("nonexistent")
                .name("Nonexistent Restaurant")
                .build();

        Location updateLocation = Location.builder()
                .locationid("nonexistent")
                .name("Nonexistent Restaurant")
                .build();

        when(locationMapper.toEntity(any(LocationDto.class))).thenReturn(updateLocation);
        when(locationService.updateLocation(any(Location.class)))
                .thenThrow(new LocationService.LocationNotFoundException("Location not found: nonexistent"));

        // When & Then
        mockMvc.perform(put("/api/v1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(locationMapper, times(1)).toEntity(any(LocationDto.class));
        verify(locationService, times(1)).updateLocation(any(Location.class));
        verify(locationMapper, never()).toDto(any(Location.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/location/{id} - Should delete location successfully")
    void testDeleteLocation_Success() throws Exception {
        // Given
        doNothing().when(locationService).deleteLocation("loc-001");

        // When & Then
        mockMvc.perform(delete("/api/v1/location/loc-001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).deleteLocation("loc-001");
    }

    @Test
    @DisplayName("DELETE /api/v1/location/{id} - Should return 404 when location not found")
    void testDeleteLocation_NotFound() throws Exception {
        // Given
        doThrow(new LocationService.LocationNotFoundException("Location not found: nonexistent"))
                .when(locationService).deleteLocation("nonexistent");

        // When & Then
        mockMvc.perform(delete("/api/v1/location/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(locationService, times(1)).deleteLocation("nonexistent");
    }

    @Test
    @DisplayName("POST /api/v1/location - Should handle location with all fields")
    void testCreateLocation_AllFields() throws Exception {
        // Given
        LocationDto fullLocationDto = LocationDto.builder()
                .name("Full Restaurant")
                .description("Complete description")
                .status("active")
                .address1("123 Main St")
                .address2("Suite 100")
                .city("Detroit")
                .locality("Downtown")
                .zip("48226")
                .region("MI")
                .country("US")
                .phone1("313-555-0100")
                .phone2("313-555-0101")
                .lat(42.3314)
                .lng(-83.0458)
                .website("https://fullrestaurant.com")
                .facebook("https://facebook.com/fullrestaurant")
                .twitter("https://twitter.com/fullrestaurant")
                .instagram("https://instagram.com/fullrestaurant")
                .opentable("https://opentable.com/fullrestaurant")
                .tripadvisor("https://tripadvisor.com/fullrestaurant")
                .yelp("https://yelp.com/fullrestaurant")
                .hours("Mon-Fri: 9am-10pm")
                .contactText("Call for reservations")
                .build();

        Location fullLocation = Location.builder()
                .name("Full Restaurant")
                .status(LocationStatus.active)
                .build();

        Location savedLocation = Location.builder()
                .locationid("loc_full123")
                .name("Full Restaurant")
                .description("Complete description")
                .status(LocationStatus.active)
                .address1("123 Main St")
                .city("Detroit")
                .createDate(OffsetDateTime.now())
                .updatedDate(OffsetDateTime.now())
                .build();

        LocationDto savedDto = LocationDto.builder()
                .locationid("loc_full123")
                .name("Full Restaurant")
                .description("Complete description")
                .status("active")
                .address1("123 Main St")
                .city("Detroit")
                .build();

        when(locationMapper.toEntity(any(LocationDto.class))).thenReturn(fullLocation);
        when(locationService.createLocation(any(Location.class))).thenReturn(savedLocation);
        when(locationMapper.toDto(savedLocation)).thenReturn(savedDto);

        // When & Then
        mockMvc.perform(post("/api/v1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullLocationDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.locationid").value("loc_full123"))
                .andExpect(jsonPath("$.data.name").value("Full Restaurant"))
                .andExpect(jsonPath("$.message").value("Location created successfully"));

        verify(locationService, times(1)).createLocation(any(Location.class));
    }

    @Test
    @DisplayName("GET /api/v1/location/{id} - Should handle special characters in location ID")
    void testGetLocationById_SpecialCharacters() throws Exception {
        // Given
        String osmId = "osm-n123456";
        Location osmLocation = Location.builder()
                .locationid(osmId)
                .name("OSM Restaurant")
                .city("Detroit")
                .status(LocationStatus.active)
                .build();

        LocationDto osmDto = LocationDto.builder()
                .locationid(osmId)
                .name("OSM Restaurant")
                .city("Detroit")
                .status("active")
                .build();

        when(locationService.getLocationById(osmId)).thenReturn(Optional.of(osmLocation));
        when(locationMapper.toDto(osmLocation)).thenReturn(osmDto);

        // When & Then
        mockMvc.perform(get("/api/v1/location/" + osmId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.locationid").value(osmId))
                .andExpect(jsonPath("$.data.name").value("OSM Restaurant"));

        verify(locationService, times(1)).getLocationById(osmId);
    }

    @Test
    @DisplayName("PUT /api/v1/location - Should update location with different status")
    void testUpdateLocation_ChangeStatus() throws Exception {
        // Given
        LocationDto updateDto = LocationDto.builder()
                .locationid("loc-001")
                .name("Test Restaurant")
                .status("temporarily_closed")
                .city("Detroit")
                .build();

        Location updateLocation = Location.builder()
                .locationid("loc-001")
                .name("Test Restaurant")
                .status(LocationStatus.temporarily_closed)
                .city("Detroit")
                .build();

        Location updatedLocation = Location.builder()
                .locationid("loc-001")
                .name("Test Restaurant")
                .status(LocationStatus.temporarily_closed)
                .city("Detroit")
                .updatedDate(OffsetDateTime.now())
                .build();

        LocationDto updatedDto = LocationDto.builder()
                .locationid("loc-001")
                .name("Test Restaurant")
                .status("temporarily_closed")
                .city("Detroit")
                .build();

        when(locationMapper.toEntity(any(LocationDto.class))).thenReturn(updateLocation);
        when(locationService.updateLocation(any(Location.class))).thenReturn(updatedLocation);
        when(locationMapper.toDto(updatedLocation)).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("temporarily_closed"))
                .andExpect(jsonPath("$.message").value("Location updated successfully"));

        verify(locationService, times(1)).updateLocation(any(Location.class));
    }
}
