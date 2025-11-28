package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.repository.LocationRepository;
import com.detroitchow.admin.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "locations", key = "'all'")
    public List<Location> getAllLocations() {
        log.info("Fetching all locations from database (cache miss)");
        return locationRepository.getAllLocations();
    }

    public Optional<Location> getLocationById(String id) {
        return locationRepository.findById(id);
    }

    @CacheEvict(value = "locations", key = "'all'")
    public Location createLocation(Location location) {
        if (location.getLocationid() == null || location.getLocationid().isEmpty()) {
            location.setLocationid(generateLocationId());
        }
        location.setCreateDate(OffsetDateTime.now());
        location.setUpdatedDate(OffsetDateTime.now());

        Location saved = locationRepository.save(location);
        log.info("Location created: {}, cache invalidated", saved.getLocationid());
        return saved;
    }

    @CacheEvict(value = "locations", key = "'all'")
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
        log.info("Location updated: {}, cache invalidated", updated.getLocationid());
        return updated;
    }

    @CacheEvict(value = "locations", key = "'all'")
    public void deleteLocation(String id) {
        if (!locationRepository.existsById(id)) {
            throw new LocationNotFoundException("Location not found: " + id);
        }
        locationRepository.deleteById(id);
        log.info("Location deleted: {}, cache invalidated", id);
    }

    private String generateLocationId() {
        return "loc_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static class LocationNotFoundException extends RuntimeException {
        public LocationNotFoundException(String message) {
            super(message);
        }
    }
}
