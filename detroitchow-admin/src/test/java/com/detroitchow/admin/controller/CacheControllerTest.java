package com.detroitchow.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CacheController.class)
@DisplayName("CacheController Tests")
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CacheManager cacheManager;

    @MockBean
    private Cache locationsCache;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache("locations")).thenReturn(locationsCache);
        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("locations"));
    }

    @Test
    @DisplayName("GET /cache/status - Should return cache status")
    void testGetCacheStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/cache/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationsCache").exists())
                .andExpect(jsonPath("$.message").value("Cache status retrieved"));

        verify(cacheManager, times(1)).getCache("locations");
    }

    @Test
    @DisplayName("GET /cache/status - Should handle cache not found")
    void testGetCacheStatus_CacheNotFound() throws Exception {
        // Given
        when(cacheManager.getCache("locations")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/cache/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationsCache.status").value("not found"))
                .andExpect(jsonPath("$.message").value("Cache status retrieved"));
    }

    @Test
    @DisplayName("DELETE /cache/clear - Should clear all caches")
    void testClearAllCaches() throws Exception {
        // When & Then
        mockMvc.perform(delete("/cache/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All caches cleared successfully"))
                .andExpect(jsonPath("$.clearedCaches").isArray());

        verify(cacheManager, atLeastOnce()).getCacheNames();
        verify(locationsCache, times(1)).clear();
    }

    @Test
    @DisplayName("DELETE /cache/clear/locations - Should clear locations cache")
    void testClearLocationsCache() throws Exception {
        // When & Then
        mockMvc.perform(delete("/cache/clear/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Locations cache cleared successfully"))
                .andExpect(jsonPath("$.cache").value("locations"));

        verify(cacheManager, times(1)).getCache("locations");
        verify(locationsCache, times(1)).clear();
    }

    @Test
    @DisplayName("DELETE /cache/clear/locations - Should handle cache not found")
    void testClearLocationsCache_NotFound() throws Exception {
        // Given
        when(cacheManager.getCache("locations")).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/cache/clear/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Locations cache not found"))
                .andExpect(jsonPath("$.cache").value("locations"));

        verify(cacheManager, times(1)).getCache("locations");
        verify(locationsCache, never()).clear();
    }

    @Test
    @DisplayName("POST /cache/evict/locations/{key} - Should evict cache entry")
    void testEvictLocationsCacheKey() throws Exception {
        // When & Then
        mockMvc.perform(post("/cache/evict/locations/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cache entry evicted successfully"))
                .andExpect(jsonPath("$.cache").value("locations"))
                .andExpect(jsonPath("$.key").value("all"));

        verify(cacheManager, times(1)).getCache("locations");
        verify(locationsCache, times(1)).evict("all");
    }

    @Test
    @DisplayName("POST /cache/evict/locations/{key} - Should handle cache not found")
    void testEvictLocationsCacheKey_CacheNotFound() throws Exception {
        // Given
        when(cacheManager.getCache("locations")).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/cache/evict/locations/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Locations cache not found"));

        verify(cacheManager, times(1)).getCache("locations");
        verify(locationsCache, never()).evict(anyString());
    }
}
