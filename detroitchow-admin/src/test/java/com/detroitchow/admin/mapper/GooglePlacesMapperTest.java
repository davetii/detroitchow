package com.detroitchow.admin.mapper;

import com.detroitchow.admin.dto.GooglePlacesDto;
import com.detroitchow.admin.entity.GooglePlaces;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GooglePlacesMapperTest {

    private GooglePlacesMapper googlePlacesMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        googlePlacesMapper = new GooglePlacesMapper();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testToDto_WithFullGooglePlaces_ShouldMapAllFields() throws Exception {
        // Given
        JsonNode txtsearchJson = objectMapper.readTree("{\"results\":[]}");
        JsonNode detailJson = objectMapper.readTree("{\"result\":{}}");
        JsonNode storeJson = objectMapper.readTree("{\"store\":{}}");

        GooglePlaces googlePlaces = GooglePlaces.builder()
                .id(123)
                .placeId("ChIJN1t_tDeuEmsRUsoyG83frY4")
                .lat("42.3314")
                .lng("-83.0458")
                .phone1("313-555-1234")
                .phone2("313-555-5678")
                .formattedAddress("1234 Woodward Ave, Detroit, MI 48226")
                .website("https://example.com")
                .googleUrl("https://maps.google.com/?cid=123456789")
                .businessStatus("OPERATIONAL")
                .txtsearchJson(txtsearchJson)
                .detailJson(detailJson)
                .storeJson(storeJson)
                .premisePlaceId("ChIJPremiseExample")
                .storePlaceId("ChIJStoreExample")
                .build();

        // When
        GooglePlacesDto dto = googlePlacesMapper.toDto(googlePlaces);

        // Then
        assertNotNull(dto);
        assertEquals(123, dto.getId());
        assertEquals("ChIJN1t_tDeuEmsRUsoyG83frY4", dto.getPlaceId());
        assertEquals("42.3314", dto.getLat());
        assertEquals("-83.0458", dto.getLng());
        assertEquals("313-555-1234", dto.getPhone1());
        assertEquals("313-555-5678", dto.getPhone2());
        assertEquals("1234 Woodward Ave, Detroit, MI 48226", dto.getFormattedAddress());
        assertEquals("https://example.com", dto.getWebsite());
        assertEquals("https://maps.google.com/?cid=123456789", dto.getGoogleUrl());
        assertEquals("OPERATIONAL", dto.getBusinessStatus());
        assertEquals(txtsearchJson, dto.getTxtsearchJson());
        assertEquals(detailJson, dto.getDetailJson());
        assertEquals(storeJson, dto.getStoreJson());
        assertEquals("ChIJPremiseExample", dto.getPremisePlaceId());
        assertEquals("ChIJStoreExample", dto.getStorePlaceId());
    }

    @Test
    void testToDto_WithNullGooglePlaces_ShouldReturnNull() {
        // When
        GooglePlacesDto dto = googlePlacesMapper.toDto(null);

        // Then
        assertNull(dto);
    }

    @Test
    void testToDto_WithMinimalGooglePlaces_ShouldMapOnlyProvidedFields() {
        // Given
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .id(456)
                .placeId("ChIJMinimalExample")
                .build();

        // When
        GooglePlacesDto dto = googlePlacesMapper.toDto(googlePlaces);

        // Then
        assertNotNull(dto);
        assertEquals(456, dto.getId());
        assertEquals("ChIJMinimalExample", dto.getPlaceId());
        assertNull(dto.getLat());
        assertNull(dto.getLng());
        assertNull(dto.getPhone1());
        assertNull(dto.getPhone2());
        assertNull(dto.getFormattedAddress());
        assertNull(dto.getWebsite());
        assertNull(dto.getGoogleUrl());
        assertNull(dto.getBusinessStatus());
        assertNull(dto.getTxtsearchJson());
        assertNull(dto.getDetailJson());
        assertNull(dto.getStoreJson());
        assertNull(dto.getPremisePlaceId());
        assertNull(dto.getStorePlaceId());
    }

    @Test
    void testToEntity_WithFullDto_ShouldMapAllFields() throws Exception {
        // Given
        JsonNode txtsearchJson = objectMapper.readTree("{\"results\":[{\"name\":\"Test\"}]}");
        JsonNode detailJson = objectMapper.readTree("{\"result\":{\"rating\":4.5}}");
        JsonNode storeJson = objectMapper.readTree("{\"store\":{\"hours\":[]}}");

        GooglePlacesDto dto = GooglePlacesDto.builder()
                .id(789)
                .placeId("ChIJAnotherExample")
                .lat("42.3500")
                .lng("-83.1000")
                .phone1("248-555-1111")
                .phone2("248-555-2222")
                .formattedAddress("5678 Main St, Ann Arbor, MI 48104")
                .website("https://restaurant.com")
                .googleUrl("https://maps.google.com/?cid=987654321")
                .businessStatus("CLOSED_TEMPORARILY")
                .txtsearchJson(txtsearchJson)
                .detailJson(detailJson)
                .storeJson(storeJson)
                .premisePlaceId("ChIJPremise2")
                .storePlaceId("ChIJStore2")
                .build();

        // When
        GooglePlaces googlePlaces = googlePlacesMapper.toEntity(dto);

        // Then
        assertNotNull(googlePlaces);
        assertEquals(789, googlePlaces.getId());
        assertEquals("ChIJAnotherExample", googlePlaces.getPlaceId());
        assertEquals("42.3500", googlePlaces.getLat());
        assertEquals("-83.1000", googlePlaces.getLng());
        assertEquals("248-555-1111", googlePlaces.getPhone1());
        assertEquals("248-555-2222", googlePlaces.getPhone2());
        assertEquals("5678 Main St, Ann Arbor, MI 48104", googlePlaces.getFormattedAddress());
        assertEquals("https://restaurant.com", googlePlaces.getWebsite());
        assertEquals("https://maps.google.com/?cid=987654321", googlePlaces.getGoogleUrl());
        assertEquals("CLOSED_TEMPORARILY", googlePlaces.getBusinessStatus());
        assertEquals(txtsearchJson, googlePlaces.getTxtsearchJson());
        assertEquals(detailJson, googlePlaces.getDetailJson());
        assertEquals(storeJson, googlePlaces.getStoreJson());
        assertEquals("ChIJPremise2", googlePlaces.getPremisePlaceId());
        assertEquals("ChIJStore2", googlePlaces.getStorePlaceId());
    }

    @Test
    void testToEntity_WithNullDto_ShouldReturnNull() {
        // When
        GooglePlaces googlePlaces = googlePlacesMapper.toEntity(null);

        // Then
        assertNull(googlePlaces);
    }

    @Test
    void testToEntity_WithMinimalDto_ShouldMapOnlyProvidedFields() {
        // Given
        GooglePlacesDto dto = GooglePlacesDto.builder()
                .id(999)
                .placeId("ChIJMinimalDto")
                .lat("42.4000")
                .build();

        // When
        GooglePlaces googlePlaces = googlePlacesMapper.toEntity(dto);

        // Then
        assertNotNull(googlePlaces);
        assertEquals(999, googlePlaces.getId());
        assertEquals("ChIJMinimalDto", googlePlaces.getPlaceId());
        assertEquals("42.4000", googlePlaces.getLat());
        assertNull(googlePlaces.getLng());
        assertNull(googlePlaces.getPhone1());
        assertNull(googlePlaces.getPhone2());
        assertNull(googlePlaces.getFormattedAddress());
        assertNull(googlePlaces.getWebsite());
        assertNull(googlePlaces.getGoogleUrl());
        assertNull(googlePlaces.getBusinessStatus());
        assertNull(googlePlaces.getTxtsearchJson());
        assertNull(googlePlaces.getDetailJson());
        assertNull(googlePlaces.getStoreJson());
        assertNull(googlePlaces.getPremisePlaceId());
        assertNull(googlePlaces.getStorePlaceId());
    }

    @Test
    void testRoundTrip_EntityToDtoToEntity_ShouldPreserveData() throws Exception {
        // Given
        JsonNode txtsearchJson = objectMapper.readTree("{\"test\":true}");
        JsonNode detailJson = objectMapper.readTree("{\"detail\":true}");
        JsonNode storeJson = objectMapper.readTree("{\"store\":true}");

        GooglePlaces originalGooglePlaces = GooglePlaces.builder()
                .id(111)
                .placeId("ChIJRoundTripTest")
                .lat("42.2000")
                .lng("-83.2000")
                .phone1("734-555-1234")
                .phone2("734-555-5678")
                .formattedAddress("100 Test St, Detroit, MI 48201")
                .website("https://test.com")
                .googleUrl("https://maps.google.com/?cid=111222333")
                .businessStatus("OPERATIONAL")
                .txtsearchJson(txtsearchJson)
                .detailJson(detailJson)
                .storeJson(storeJson)
                .premisePlaceId("ChIJPremiseTest")
                .storePlaceId("ChIJStoreTest")
                .build();

        // When
        GooglePlacesDto dto = googlePlacesMapper.toDto(originalGooglePlaces);
        GooglePlaces resultGooglePlaces = googlePlacesMapper.toEntity(dto);

        // Then
        assertNotNull(resultGooglePlaces);
        assertEquals(originalGooglePlaces.getId(), resultGooglePlaces.getId());
        assertEquals(originalGooglePlaces.getPlaceId(), resultGooglePlaces.getPlaceId());
        assertEquals(originalGooglePlaces.getLat(), resultGooglePlaces.getLat());
        assertEquals(originalGooglePlaces.getLng(), resultGooglePlaces.getLng());
        assertEquals(originalGooglePlaces.getPhone1(), resultGooglePlaces.getPhone1());
        assertEquals(originalGooglePlaces.getPhone2(), resultGooglePlaces.getPhone2());
        assertEquals(originalGooglePlaces.getFormattedAddress(), resultGooglePlaces.getFormattedAddress());
        assertEquals(originalGooglePlaces.getWebsite(), resultGooglePlaces.getWebsite());
        assertEquals(originalGooglePlaces.getGoogleUrl(), resultGooglePlaces.getGoogleUrl());
        assertEquals(originalGooglePlaces.getBusinessStatus(), resultGooglePlaces.getBusinessStatus());
        assertEquals(originalGooglePlaces.getTxtsearchJson(), resultGooglePlaces.getTxtsearchJson());
        assertEquals(originalGooglePlaces.getDetailJson(), resultGooglePlaces.getDetailJson());
        assertEquals(originalGooglePlaces.getStoreJson(), resultGooglePlaces.getStoreJson());
        assertEquals(originalGooglePlaces.getPremisePlaceId(), resultGooglePlaces.getPremisePlaceId());
        assertEquals(originalGooglePlaces.getStorePlaceId(), resultGooglePlaces.getStorePlaceId());
    }

    @Test
    void testRoundTrip_DtoToEntityToDto_ShouldPreserveData() throws Exception {
        // Given
        JsonNode txtsearchJson = objectMapper.readTree("{\"original\":true}");
        JsonNode detailJson = objectMapper.readTree("{\"original_detail\":true}");
        JsonNode storeJson = objectMapper.readTree("{\"original_store\":true}");

        GooglePlacesDto originalDto = GooglePlacesDto.builder()
                .id(222)
                .placeId("ChIJDtoRoundTrip")
                .lat("42.5000")
                .lng("-83.5000")
                .phone1("586-555-9999")
                .phone2("586-555-8888")
                .formattedAddress("200 Round St, Detroit, MI 48202")
                .website("https://roundtrip.com")
                .googleUrl("https://maps.google.com/?cid=999888777")
                .businessStatus("CLOSED_PERMANENTLY")
                .txtsearchJson(txtsearchJson)
                .detailJson(detailJson)
                .storeJson(storeJson)
                .premisePlaceId("ChIJOriginalPremise")
                .storePlaceId("ChIJOriginalStore")
                .build();

        // When
        GooglePlaces googlePlaces = googlePlacesMapper.toEntity(originalDto);
        GooglePlacesDto resultDto = googlePlacesMapper.toDto(googlePlaces);

        // Then
        assertNotNull(resultDto);
        assertEquals(originalDto.getId(), resultDto.getId());
        assertEquals(originalDto.getPlaceId(), resultDto.getPlaceId());
        assertEquals(originalDto.getLat(), resultDto.getLat());
        assertEquals(originalDto.getLng(), resultDto.getLng());
        assertEquals(originalDto.getPhone1(), resultDto.getPhone1());
        assertEquals(originalDto.getPhone2(), resultDto.getPhone2());
        assertEquals(originalDto.getFormattedAddress(), resultDto.getFormattedAddress());
        assertEquals(originalDto.getWebsite(), resultDto.getWebsite());
        assertEquals(originalDto.getGoogleUrl(), resultDto.getGoogleUrl());
        assertEquals(originalDto.getBusinessStatus(), resultDto.getBusinessStatus());
        assertEquals(originalDto.getTxtsearchJson(), resultDto.getTxtsearchJson());
        assertEquals(originalDto.getDetailJson(), resultDto.getDetailJson());
        assertEquals(originalDto.getStoreJson(), resultDto.getStoreJson());
        assertEquals(originalDto.getPremisePlaceId(), resultDto.getPremisePlaceId());
        assertEquals(originalDto.getStorePlaceId(), resultDto.getStorePlaceId());
    }

    @Test
    void testToDto_WithComplexJsonData_ShouldPreserveJsonNodes() throws Exception {
        // Given
        JsonNode complexTxtsearch = objectMapper.readTree(
                "{\"results\":[{\"name\":\"Test Restaurant\",\"rating\":4.5,\"types\":[\"restaurant\",\"food\"]}]}"
        );
        JsonNode complexDetail = objectMapper.readTree(
                "{\"result\":{\"opening_hours\":{\"weekday_text\":[\"Monday: 9:00 AM – 5:00 PM\"]}}}"
        );

        GooglePlaces googlePlaces = GooglePlaces.builder()
                .id(333)
                .placeId("ChIJComplexJson")
                .txtsearchJson(complexTxtsearch)
                .detailJson(complexDetail)
                .build();

        // When
        GooglePlacesDto dto = googlePlacesMapper.toDto(googlePlaces);

        // Then
        assertNotNull(dto);
        assertEquals(complexTxtsearch, dto.getTxtsearchJson());
        assertEquals(complexDetail, dto.getDetailJson());
    }

    @Test
    void testToDto_WithNullJsonFields_ShouldMapAsNull() {
        // Given
        GooglePlaces googlePlaces = GooglePlaces.builder()
                .id(444)
                .placeId("ChIJNullJsonTest")
                .txtsearchJson(null)
                .detailJson(null)
                .storeJson(null)
                .build();

        // When
        GooglePlacesDto dto = googlePlacesMapper.toDto(googlePlaces);

        // Then
        assertNotNull(dto);
        assertEquals(444, dto.getId());
        assertNull(dto.getTxtsearchJson());
        assertNull(dto.getDetailJson());
        assertNull(dto.getStoreJson());
    }
}
