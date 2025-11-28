package com.detroitchow.admin.repository;

import com.detroitchow.admin.entity.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LocationRepositoryTest {

    @Autowired
    LocationRepository repo;

    @Test
    public void ensureFindByLocationidreturnsExpected() {
        Optional<Location> location = repo.findByLocationid("loc-001");
        assertFalse(location.isEmpty());
        assertEquals("loc-001", location.get().getLocationid());
        assertEquals("Detroit", location.get().getCity());
        assertEquals("118 W Lafayette Blvd", location.get().getAddress1());
        assertEquals("Lafayette Coney Island", location.get().getName());
        assertEquals("313-964-8198", location.get().getPhone1());
        assertEquals("active", location.get().getOperatingStatus().toString());
        assertEquals("48226", location.get().getZip());
        assertEquals("MI", location.get().getRegion());
        assertEquals("US", location.get().getCountry());
        assertEquals("42.3297", location.get().getLat());
        assertEquals("-83.0458", location.get().getLng());
        assertEquals("https://www.coneydetroit.com", location.get().getWebsite());
        assertEquals("https://instagram.com/lafayetteconeyisland", location.get().getInstagram());
        assertEquals("https://facebook.com/lafayetteconeyisland", location.get().getFacebook());
    }

    @Test
    void ensureGetALLReturnsExpected() {
        assertEquals(8, repo.getAllLocations().size());
    }

}