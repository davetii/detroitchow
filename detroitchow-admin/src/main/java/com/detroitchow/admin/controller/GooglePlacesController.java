package com.detroitchow.admin.controller;

import com.detroitchow.admin.dto.GooglePlacesDto;
import com.detroitchow.admin.entity.GooglePlaces;
import com.detroitchow.admin.mapper.GooglePlacesMapper;
import com.detroitchow.admin.service.GooglePlacesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class GooglePlacesController {

    private final GooglePlacesService googlePlacesService;
    private final GooglePlacesMapper googlePlacesMapper;

    /**
     * GET /googleplaces - Get all Google Places data
     */
    @GetMapping("/googleplaces")
    public ResponseEntity<List<GooglePlacesDto>> getAllGooglePlaces() {
        List<GooglePlacesDto> googlePlacesDtos = googlePlacesService.getAllGooglePlaces()
                .stream()
                .map(googlePlacesMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(googlePlacesDtos);
    }

    /**
     * GET /googleplace/{locationid} - Get Google Places data by location ID
     */
    @GetMapping("/googleplace/{locationid}")
    public ResponseEntity<?> getGooglePlaceByLocationId(@PathVariable String locationid) {
        log.debug("Getting Google Places data by location ID: {}", locationid);
        return googlePlacesService.getGooglePlacesByLocationId(locationid)
                .map(googlePlaces -> {
                    GooglePlacesDto dto = googlePlacesMapper.toDto(googlePlaces);
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", dto);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /googleplace/place/{place_id} - Get Google Places data by Google Place ID
     */
    @GetMapping("/googleplace/place/{place_id}")
    public ResponseEntity<?> getGooglePlaceByPlaceId(@PathVariable("place_id") String placeId) {
        log.debug("Getting Google Places data by place ID: {}", placeId);
        return googlePlacesService.getGooglePlacesByPlaceId(placeId)
                .map(googlePlaces -> {
                    GooglePlacesDto dto = googlePlacesMapper.toDto(googlePlaces);
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", dto);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /googleplace - Create a new Google Places entry
     */
    @PostMapping("/googleplace")
    public ResponseEntity<Map<String, Object>> createGooglePlace(@RequestBody GooglePlacesDto googlePlacesDto) {
        log.debug("Creating new Google Places entry for location: {}", googlePlacesDto.getLocationid());

        GooglePlaces googlePlaces = googlePlacesMapper.toEntity(googlePlacesDto);
        GooglePlaces saved = googlePlacesService.saveGooglePlaces(googlePlacesDto.getLocationid(), googlePlaces);
        GooglePlacesDto savedDto = googlePlacesMapper.toDto(saved);

        Map<String, Object> response = new HashMap<>();
        response.put("data", savedDto);
        response.put("message", "Google Places data created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /googleplace - Update an existing Google Places entry
     */
    @PutMapping("/googleplace")
    public ResponseEntity<?> updateGooglePlace(@RequestBody GooglePlacesDto googlePlacesDto) {
        log.debug("Updating Google Places data: {}", googlePlacesDto.getId());

        try {
            GooglePlaces googlePlaces = googlePlacesMapper.toEntity(googlePlacesDto);
            GooglePlaces updated = googlePlacesService.updateGooglePlaces(googlePlacesDto.getId(), googlePlaces);
            GooglePlacesDto updatedDto = googlePlacesMapper.toDto(updated);

            Map<String, Object> response = new HashMap<>();
            response.put("data", updatedDto);
            response.put("message", "Google Places data updated successfully");

            return ResponseEntity.ok(response);
        } catch (GooglePlacesService.GooglePlacesNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /googleplace - Delete a Google Places entry
     */
    @DeleteMapping("/googleplace")
    public ResponseEntity<?> deleteGooglePlace(@RequestBody Map<String, String> request) {
        String locationid = request.get("locationid");
        log.debug("Deleting Google Places data for location: {}", locationid);

        try {
            googlePlacesService.deleteGooglePlacesByLocationId(locationid);
            return ResponseEntity.noContent().build();
        } catch (GooglePlacesService.GooglePlacesNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
