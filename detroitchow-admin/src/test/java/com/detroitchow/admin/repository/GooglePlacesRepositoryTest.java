package com.detroitchow.admin.repository;

import com.detroitchow.admin.entity.GooglePlaces;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GooglePlacesRepositoryTest {

    @Autowired
    GooglePlacesRepository repo;

    @Test
    void findByLocationid() {
       Optional<GooglePlaces> place=  repo.findByLocationid("loc-001");
       assertTrue(place.isPresent());
       assertEquals("loc-001", place.get().getLocationid());
       assertEquals("ChIJN5X_gFcJO4gRZn0F5hC0k9c", place.get().getPlaceId());
       assertEquals("42.3297", place.get().getLat());
       assertEquals("-83.0458", place.get().getLng());
       assertEquals("+13139648198", place.get().getPhone1());
       assertEquals("118 W Lafayette Blvd, Detroit, MI 48226", place.get().getFormattedAddress());
       assertEquals("https://www.coneydetroit.com", place.get().getWebsite());
       assertEquals("OPERATIONAL", place.get().getBusinessStatus());
    }

    @Test
    void findByPlaceId() {
        Optional<GooglePlaces> place=  repo.findByPlaceId("ChIJN5X_gFcJO4gRZn0F5hC0k9c");
        assertTrue(place.isPresent());
        assertEquals("loc-001", place.get().getLocationid());
        assertEquals("ChIJN5X_gFcJO4gRZn0F5hC0k9c", place.get().getPlaceId());
        assertEquals("42.3297", place.get().getLat());
        assertEquals("-83.0458", place.get().getLng());
        assertEquals("+13139648198", place.get().getPhone1());
        assertEquals("118 W Lafayette Blvd, Detroit, MI 48226", place.get().getFormattedAddress());
        assertEquals("https://www.coneydetroit.com", place.get().getWebsite());
        assertEquals("OPERATIONAL", place.get().getBusinessStatus());
    }
}