package com.detroitchow.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class CacheController {

    private final CacheManager cacheManager;

    /**
     * GET /cache/status - Get cache statistics
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCacheStatus() {
        Map<String, Object> response = new HashMap<>();

        var locationsCache = cacheManager.getCache("locations");

        Map<String, Object> locationsCacheInfo = new HashMap<>();
        if (locationsCache != null) {
            locationsCacheInfo.put("name", "locations");
            locationsCacheInfo.put("type", locationsCache.getClass().getSimpleName());
            locationsCacheInfo.put("status", "active");
        } else {
            locationsCacheInfo.put("status", "not found");
        }

        response.put("locationsCache", locationsCacheInfo);
        response.put("message", "Cache status retrieved");

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /cache/clear - Clear all caches
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        log.info("Clearing all caches");

        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cleared cache: {}", cacheName);
            }
        });

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All caches cleared successfully");
        response.put("clearedCaches", cacheManager.getCacheNames());

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /cache/clear/locations - Clear locations cache only
     */
    @DeleteMapping("/clear/locations")
    public ResponseEntity<Map<String, Object>> clearLocationsCache() {
        log.info("Clearing locations cache");

        var cache = cacheManager.getCache("locations");
        Map<String, Object> response = new HashMap<>();

        if (cache != null) {
            cache.clear();
            response.put("message", "Locations cache cleared successfully");
            response.put("cache", "locations");
            log.info("Locations cache cleared");
        } else {
            response.put("message", "Locations cache not found");
            response.put("cache", "locations");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * POST /cache/evict/locations/{key} - Evict a specific cache entry
     */
    @PostMapping("/evict/locations/{key}")
    public ResponseEntity<Map<String, Object>> evictLocationsCacheKey(@PathVariable String key) {
        log.info("Evicting cache key: {}", key);

        var cache = cacheManager.getCache("locations");
        Map<String, Object> response = new HashMap<>();

        if (cache != null) {
            cache.evict(key);
            response.put("message", "Cache entry evicted successfully");
            response.put("cache", "locations");
            response.put("key", key);
            log.info("Evicted cache entry: locations/{}", key);
        } else {
            response.put("message", "Locations cache not found");
        }

        return ResponseEntity.ok(response);
    }
}
