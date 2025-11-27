package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.GooglePlaces;
import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.repository.GooglePlacesRepository;
import com.detroitchow.admin.repository.LocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("GooglePlacesService Tests")
class GooglePlacesServiceTest {

    @Autowired
    private GooglePlacesService googlePlacesService;

    @Autowired
    private GooglePlacesRepository googlePlacesRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should save Google Places data for a location")
    void testSaveGooglePlaces_Success() {
        // Given
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJTest123")
                .lat("42.3314")
                .lng("-83.0458")
                .phone1("313-555-0100")
                .formattedAddress("123 Test St, Detroit, MI 48226")
                .website("https://example.com")
                .googleUrl("https://maps.google.com/?cid=123")
                .businessStatus("OPERATIONAL")
                .build();

        // When
        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-001", googlePlaces);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlaceId()).isEqualTo("ChIJTest123");
        assertThat(saved.getLat()).isEqualTo("42.3314");
        assertThat(saved.getLng()).isEqualTo("-83.0458");
        assertThat(saved.getPhone1()).isEqualTo("313-555-0100");
        assertThat(saved.getFormattedAddress()).isEqualTo("123 Test St, Detroit, MI 48226");
        assertThat(saved.getWebsite()).isEqualTo("https://example.com");
        assertThat(saved.getGoogleUrl()).isEqualTo("https://maps.google.com/?cid=123");
        assertThat(saved.getBusinessStatus()).isEqualTo("OPERATIONAL");
        assertThat(saved.getCreatedAt()).isNotNull();

        // Verify it was saved to database
        Optional<GooglePlaces> fromDb = googlePlacesRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getPlaceId()).isEqualTo("ChIJTest123");

        // Verify via location query (locationid is set via FK relationship)
        Optional<GooglePlaces> byLocation = googlePlacesRepository.findByLocationid("loc-001");
        assertThat(byLocation).isPresent();
        assertThat(byLocation.get().getPlaceId()).isEqualTo("ChIJTest123");
    }

    @Test
    @DisplayName("Should save Google Places with JSON fields")
    void testSaveGooglePlaces_WithJsonFields() {
        // Given
        ObjectNode txtsearchJson = objectMapper.createObjectNode();
        txtsearchJson.put("name", "Test Restaurant");
        txtsearchJson.put("rating", 4.5);

        ObjectNode detailJson = objectMapper.createObjectNode();
        detailJson.put("opening_hours", "Mon-Fri 9am-10pm");

        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJTest456")
                .lat("42.3314")
                .lng("-83.0458")
                .txtsearchJson(txtsearchJson)
                .detailJson(detailJson)
                .build();

        // When
        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-002", googlePlaces);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getTxtsearchJson()).isNotNull();
        assertThat(saved.getTxtsearchJson().get("name").asText()).isEqualTo("Test Restaurant");
        assertThat(saved.getTxtsearchJson().get("rating").asDouble()).isEqualTo(4.5);
        assertThat(saved.getDetailJson()).isNotNull();
        assertThat(saved.getDetailJson().get("opening_hours").asText()).isEqualTo("Mon-Fri 9am-10pm");
    }

    @Test
    @DisplayName("Should throw exception when saving Google Places for non-existent location")
    void testSaveGooglePlaces_LocationNotFound() {
        // Given
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJTest789")
                .build();

        // When & Then
        assertThatThrownBy(() -> googlePlacesService.saveGooglePlaces("nonexistent-id", googlePlaces))
                .isInstanceOf(GooglePlacesService.GooglePlacesNotFoundException.class)
                .hasMessageContaining("Location not found")
                .hasMessageContaining("nonexistent-id");
    }

    @Test
    @DisplayName("Should get Google Places by location ID")
    void testGetGooglePlacesByLocationId_Success() {
        // Given: Create Google Places data first
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJGetTest123")
                .lat("42.3314")
                .lng("-83.0458")
                .build();

        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-003", googlePlaces);

        // When
        Optional<GooglePlaces> result = googlePlacesService.getGooglePlacesByLocationId("loc-003");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPlaceId()).isEqualTo("ChIJGetTest123");
    }

    @Test
    @DisplayName("Should return empty when getting Google Places for location without data")
    void testGetGooglePlacesByLocationId_NotFound() {
        // When - loc-004 has no Google Places data
        Optional<GooglePlaces> result = googlePlacesService.getGooglePlacesByLocationId("loc-004");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get Google Places by Google Place ID")
    void testGetGooglePlacesByPlaceId_Success() {
        // Given: Create Google Places data first
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJUniquePlaceId")
                .lat("42.3314")
                .lng("-83.0458")
                .build();

        googlePlacesService.saveGooglePlaces("loc-005", googlePlaces);

        // When
        Optional<GooglePlaces> result = googlePlacesService.getGooglePlacesByPlaceId("ChIJUniquePlaceId");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPlaceId()).isEqualTo("ChIJUniquePlaceId");
    }

    @Test
    @DisplayName("Should return empty when getting Google Places by non-existent place ID")
    void testGetGooglePlacesByPlaceId_NotFound() {
        // When
        Optional<GooglePlaces> result = googlePlacesService.getGooglePlacesByPlaceId("NonExistentPlaceId");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update Google Places data")
    void testUpdateGooglePlaces_Success() {
        // Given: Create Google Places data first
        GooglePlaces original = GooglePlaces.builder()
                .placeId("ChIJOriginal")
                .lat("42.3314")
                .lng("-83.0458")
                .phone1("313-555-0100")
                .formattedAddress("Original Address")
                .website("https://original.com")
                .businessStatus("OPERATIONAL")
                .build();

        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-006", original);
        Integer savedId = saved.getId();

        // When: Update with new data
        GooglePlaces updates = GooglePlaces.builder()
                .placeId("ChIJUpdated")
                .lat("42.4444")
                .lng("-83.1111")
                .phone1("313-555-9999")
                .phone2("313-555-8888")
                .formattedAddress("Updated Address")
                .website("https://updated.com")
                .googleUrl("https://maps.google.com/?cid=999")
                .businessStatus("CLOSED_TEMPORARILY")
                .build();

        GooglePlaces updated = googlePlacesService.updateGooglePlaces(savedId, updates);

        // Then
        assertThat(updated.getId()).isEqualTo(savedId);
        assertThat(updated.getPlaceId()).isEqualTo("ChIJUpdated");
        assertThat(updated.getLat()).isEqualTo("42.4444");
        assertThat(updated.getLng()).isEqualTo("-83.1111");
        assertThat(updated.getPhone1()).isEqualTo("313-555-9999");
        assertThat(updated.getPhone2()).isEqualTo("313-555-8888");
        assertThat(updated.getFormattedAddress()).isEqualTo("Updated Address");
        assertThat(updated.getWebsite()).isEqualTo("https://updated.com");
        assertThat(updated.getGoogleUrl()).isEqualTo("https://maps.google.com/?cid=999");
        assertThat(updated.getBusinessStatus()).isEqualTo("CLOSED_TEMPORARILY");

        // Verify in database
        GooglePlaces fromDb = googlePlacesRepository.findById(savedId).orElseThrow();
        assertThat(fromDb.getPlaceId()).isEqualTo("ChIJUpdated");
        assertThat(fromDb.getWebsite()).isEqualTo("https://updated.com");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent Google Places")
    void testUpdateGooglePlaces_NotFound() {
        // Given
        GooglePlaces updates = GooglePlaces.builder()
                .placeId("ChIJUpdated")
                .build();

        // When & Then
        assertThatThrownBy(() -> googlePlacesService.updateGooglePlaces(99999, updates))
                .isInstanceOf(GooglePlacesService.GooglePlacesNotFoundException.class)
                .hasMessageContaining("Google Places not found with id")
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("Should delete Google Places by location ID")
    void testDeleteGooglePlacesByLocationId_Success() {
        // Given: Create Google Places data first
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJDeleteTest")
                .lat("42.3314")
                .lng("-83.0458")
                .build();

        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-007", googlePlaces);

        // Verify it exists
        assertThat(googlePlacesRepository.findByLocationid("loc-007")).isPresent();

        // When
        googlePlacesService.deleteGooglePlacesByLocationId("loc-007");

        // Then
        assertThat(googlePlacesRepository.findByLocationid("loc-007")).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when deleting Google Places by non-existent location ID")
    void testDeleteGooglePlacesByLocationId_NotFound() {
        // When & Then
        assertThatThrownBy(() -> googlePlacesService.deleteGooglePlacesByLocationId("nonexistent-location"))
                .isInstanceOf(GooglePlacesService.GooglePlacesNotFoundException.class)
                .hasMessageContaining("Google Places not found for location")
                .hasMessageContaining("nonexistent-location");
    }

    @Test
    @DisplayName("Should delete Google Places by ID")
    void testDeleteGooglePlaces_Success() {
        // Given: Create Google Places data first
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJDeleteByIdTest")
                .lat("42.3314")
                .lng("-83.0458")
                .build();

        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-008", googlePlaces);
        Integer savedId = saved.getId();

        // Verify it exists
        assertThat(googlePlacesRepository.existsById(savedId)).isTrue();

        // When
        googlePlacesService.deleteGooglePlaces(savedId);

        // Then
        assertThat(googlePlacesRepository.existsById(savedId)).isFalse();
        assertThat(googlePlacesRepository.findById(savedId)).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent Google Places by ID")
    void testDeleteGooglePlaces_NotFound() {
        // When & Then
        assertThatThrownBy(() -> googlePlacesService.deleteGooglePlaces(99999))
                .isInstanceOf(GooglePlacesService.GooglePlacesNotFoundException.class)
                .hasMessageContaining("Google Places not found with id")
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("Should save Google Places with minimal fields")
    void testSaveGooglePlaces_MinimalFields() {
        // Given
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .placeId("ChIJMinimal")
                .build();

        // When
        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-004", googlePlaces);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlaceId()).isEqualTo("ChIJMinimal");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update Google Places with JSON fields")
    void testUpdateGooglePlaces_WithJsonFields() {
        // Given: Create original
        GooglePlaces original = GooglePlaces.builder()
                .placeId("ChIJJsonUpdate")
                .build();

        GooglePlaces saved = googlePlacesService.saveGooglePlaces("loc-002", original);

        // When: Update with JSON data
        ObjectNode newTxtsearchJson = objectMapper.createObjectNode();
        newTxtsearchJson.put("updated", true);
        newTxtsearchJson.put("version", 2);

        GooglePlaces updates = GooglePlaces.builder()
                .placeId("ChIJJsonUpdate")
                .txtsearchJson(newTxtsearchJson)
                .build();

        GooglePlaces updated = googlePlacesService.updateGooglePlaces(saved.getId(), updates);

        // Then
        assertThat(updated.getTxtsearchJson()).isNotNull();
        assertThat(updated.getTxtsearchJson().get("updated").asBoolean()).isTrue();
        assertThat(updated.getTxtsearchJson().get("version").asInt()).isEqualTo(2);
    }
}
