package com.detroitchow.admin.controller;

import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.service.LocationService;
import com.detroitchow.admin.dto.LocationDto;
import com.detroitchow.admin.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class LocationsController {

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    /**
     * GET /locations - Get all locations with optional filtering and pagination
     */
    @GetMapping("/locations")
    public ResponseEntity<Map<String, Object>> getAllLocations(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        log.debug("Getting all locations with status={}, limit={}, offset={}", status, limit, offset);
        
        Page<Location> locations = locationService.getAllLocations(status, limit, offset);
        
        List<LocationDto> locationDtos = locations.getContent().stream()
                .map(locationMapper::toDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", locationDtos);
        response.put("total", locations.getTotalElements());
        response.put("limit", limit);
        response.put("offset", offset);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /location/{id} - Get a specific location by ID
     */
    @GetMapping("/location/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable String id) {
        log.debug("Getting location by ID: {}", id);
        
        return locationService.getLocationById(id)
                .map(location -> {
                    LocationDto dto = locationMapper.toDto(location);
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", dto);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /location - Create a new location
     */
    @PostMapping("/location")
    public ResponseEntity<Map<String, Object>> createLocation(@RequestBody LocationDto locationDto) {
        log.debug("Creating new location: {}", locationDto.getName());
        
        Location location = locationMapper.toEntity(locationDto);
        Location saved = locationService.createLocation(location);
        
        LocationDto savedDto = locationMapper.toDto(saved);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", savedDto);
        response.put("message", "Location created successfully");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /location - Update an existing location
     */
    @PutMapping("/location")
    public ResponseEntity<?> updateLocation(@RequestBody LocationDto locationDto) {
        log.debug("Updating location: {}", locationDto.getLocationid());
        
        try {
            Location location = locationMapper.toEntity(locationDto);
            Location updated = locationService.updateLocation(location);
            
            LocationDto updatedDto = locationMapper.toDto(updated);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", updatedDto);
            response.put("message", "Location updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (LocationService.LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /location/{id} - Delete a location
     */
    @DeleteMapping("/location/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable String id) {
        log.debug("Deleting location: {}", id);
        
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (LocationService.LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
