package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.repository.LocationRepository;
import com.detroitchow.admin.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    public Page<Location> getAllLocations(String status, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        
        if (status != null && !status.isEmpty()) {
            Location.LocationStatus locationStatus = Location.LocationStatus.valueOf(status);
            return locationRepository.findByStatus(locationStatus, pageable);
        }
        
        return locationRepository.findAll(pageable);
    }

    public Optional<Location> getLocationById(String id) {
        return locationRepository.findById(id);
    }

    public Location createLocation(Location location) {
        if (location.getLocationid() == null || location.getLocationid().isEmpty()) {
            location.setLocationid(generateLocationId());
        }
        location.setCreateDate(OffsetDateTime.now());
        location.setUpdatedDate(OffsetDateTime.now());
        
        Location saved = locationRepository.save(location);
        log.info("Location created: {}", saved.getLocationid());
        return saved;
    }

    public Location updateLocation(Location location) {
        Optional<Location> existing = locationRepository.findById(location.getLocationid());
        
        if (existing.isEmpty()) {
            throw new LocationNotFoundException("Location not found: " + location.getLocationid());
        }

        Location existingLocation = existing.get();
        
        // Update fields
        existingLocation.setName(location.getName());
        existingLocation.setDescription(location.getDescription());
        existingLocation.setStatus(location.getStatus());
        existingLocation.setAddress1(location.getAddress1());
        existingLocation.setAddress2(location.getAddress2());
        existingLocation.setCity(location.getCity());
        existingLocation.setLocality(location.getLocality());
        existingLocation.setZip(location.getZip());
        existingLocation.setRegion(location.getRegion());
        existingLocation.setCountry(location.getCountry());
        existingLocation.setPhone1(location.getPhone1());
        existingLocation.setPhone2(location.getPhone2());
        existingLocation.setLat(location.getLat());
        existingLocation.setLng(location.getLng());
        existingLocation.setWebsite(location.getWebsite());
        existingLocation.setFacebook(location.getFacebook());
        existingLocation.setTwitter(location.getTwitter());
        existingLocation.setInstagram(location.getInstagram());
        existingLocation.setOpentable(location.getOpentable());
        existingLocation.setTripadvisor(location.getTripadvisor());
        existingLocation.setYelp(location.getYelp());
        existingLocation.setHours(location.getHours());
        existingLocation.setContactText(location.getContactText());
        
        existingLocation.setUpdatedDate(OffsetDateTime.now());

        Location updated = locationRepository.save(existingLocation);
        log.info("Location updated: {}", updated.getLocationid());
        return updated;
    }

    public void deleteLocation(String id) {
        if (!locationRepository.existsById(id)) {
            throw new LocationNotFoundException("Location not found: " + id);
        }
        locationRepository.deleteById(id);
        log.info("Location deleted: {}", id);
    }

    private String generateLocationId() {
        return "loc_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public List<Location> getLocationsByCity(String city) {
        return locationRepository.findByCity(city);
    }

    public List<Location> getLocationsByRegion(String region) {
        return locationRepository.findByRegion(region);
    }

    public static class LocationNotFoundException extends RuntimeException {
        public LocationNotFoundException(String message) {
            super(message);
        }
    }
}
