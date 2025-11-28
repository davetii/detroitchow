package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.GooglePlaces;
import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.repository.GooglePlacesRepository;
import com.detroitchow.admin.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GooglePlacesService {

    private final GooglePlacesRepository googlePlacesRepository;
    private final LocationRepository locationRepository;

    /**
     * Get all Google Places data
     */
    public List<GooglePlaces> getAllGooglePlaces() {
        return googlePlacesRepository.findAll();
    }

    /**
     * Get Google Places data for a location
     */
    public Optional<GooglePlaces> getGooglePlacesByLocationId(String locationId) {
        return googlePlacesRepository.findByLocationid(locationId);
    }

    /**
     * Get Google Places data by Google Place ID
     */
    public Optional<GooglePlaces> getGooglePlacesByPlaceId(String placeId) {
        return googlePlacesRepository.findByPlaceId(placeId);
    }

    /**
     * Create or update Google Places data for a location
     */
    public GooglePlaces saveGooglePlaces(String locationId, GooglePlaces googlePlaces) {
        // Check if Google Places already exists for this location
        Optional<GooglePlaces> existing = googlePlacesRepository.findByLocationid(locationId);
        if (existing.isPresent()) {
            // Update existing record instead of creating duplicate
            return updateGooglePlaces(existing.get().getId(), googlePlaces);
        }

        // Verify location exists
        Optional<Location> locationOpt = locationRepository.findById(locationId);
        if (locationOpt.isEmpty()) {
            throw new GooglePlacesNotFoundException("Location not found: " + locationId);
        }

        // Set the locationid directly (no more relationship)
        googlePlaces.setLocationid(locationId);
        if (googlePlaces.getCreatedAt() == null) {
            googlePlaces.setCreatedAt(LocalDateTime.now());
        }

        GooglePlaces saved = googlePlacesRepository.save(googlePlaces);
        log.info("Google Places data saved for location: {}", locationId);
        return saved;
    }

    /**
     * Update existing Google Places data
     */
    public GooglePlaces updateGooglePlaces(Integer id, GooglePlaces googlePlaces) {
        Optional<GooglePlaces> existing = googlePlacesRepository.findById(id);

        if (existing.isEmpty()) {
            throw new GooglePlacesNotFoundException("Google Places not found with id: " + id);
        }

        GooglePlaces existingGooglePlaces = existing.get();

        // Update fields
        existingGooglePlaces.setPlaceId(googlePlaces.getPlaceId());
        existingGooglePlaces.setLat(googlePlaces.getLat());
        existingGooglePlaces.setLng(googlePlaces.getLng());
        existingGooglePlaces.setPhone1(googlePlaces.getPhone1());
        existingGooglePlaces.setPhone2(googlePlaces.getPhone2());
        existingGooglePlaces.setFormattedAddress(googlePlaces.getFormattedAddress());
        existingGooglePlaces.setWebsite(googlePlaces.getWebsite());
        existingGooglePlaces.setGoogleUrl(googlePlaces.getGoogleUrl());
        existingGooglePlaces.setBusinessStatus(googlePlaces.getBusinessStatus());
        existingGooglePlaces.setTxtsearchJson(googlePlaces.getTxtsearchJson());
        existingGooglePlaces.setDetailJson(googlePlaces.getDetailJson());
        existingGooglePlaces.setStoreJson(googlePlaces.getStoreJson());
        existingGooglePlaces.setPremisePlaceId(googlePlaces.getPremisePlaceId());
        existingGooglePlaces.setStorePlaceId(googlePlaces.getStorePlaceId());

        GooglePlaces updated = googlePlacesRepository.save(existingGooglePlaces);
        log.info("Google Places data updated: {}", id);
        return updated;
    }

    /**
     * Delete Google Places data by location ID
     */
    public void deleteGooglePlacesByLocationId(String locationId) {
        Optional<GooglePlaces> existing = googlePlacesRepository.findByLocationid(locationId);
        if (existing.isEmpty()) {
            throw new GooglePlacesNotFoundException("Google Places not found for location: " + locationId);
        }

        googlePlacesRepository.deleteByLocationid(locationId);
        log.info("Google Places data deleted for location: {}", locationId);
    }

    /**
     * Delete Google Places data by ID
     */
    public void deleteGooglePlaces(Integer id) {
        if (!googlePlacesRepository.existsById(id)) {
            throw new GooglePlacesNotFoundException("Google Places not found with id: " + id);
        }

        googlePlacesRepository.deleteById(id);
        log.info("Google Places data deleted: {}", id);
    }

    /**
     * Custom exception for Google Places not found
     */
    public static class GooglePlacesNotFoundException extends RuntimeException {
        public GooglePlacesNotFoundException(String message) {
            super(message);
        }
    }
}
