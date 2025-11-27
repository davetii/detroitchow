package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.entity.Menu;
import com.detroitchow.admin.repository.LocationRepository;
import com.detroitchow.admin.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final LocationRepository locationRepository;

    /**
     * Get all menus for a location, ordered by priority
     */
    public List<Menu> getMenusByLocationId(String locationId) {
        Optional<Location> location = locationRepository.findById(locationId);
        if (location.isEmpty()) {
            throw new MenuNotFoundException("Location not found: " + locationId);
        }
        
        return menuRepository.findByLocationidOrderByPriority(locationId);
    }

    /**
     * Create a new menu for a location
     */
    public Menu createMenu(String locationId, Menu menu) {
        Optional<Location> locationOpt = locationRepository.findById(locationId);
        if (locationOpt.isEmpty()) {
            throw new MenuNotFoundException("Location not found: " + locationId);
        }

        Location location = locationOpt.get();

        // Set the location relationship (this will populate locationid via the FK)
        menu.setLocation(location);
        menu.setCreateDate(OffsetDateTime.now());
        menu.setUpdatedDate(OffsetDateTime.now());

        if (menu.getPriority() == null) {
            // Find the highest priority and add 1
            List<Menu> existingMenus = menuRepository.findByLocationidOrderByPriority(locationId);
            menu.setPriority(existingMenus.isEmpty() ? 0 : existingMenus.size());
        }

        Menu saved = menuRepository.save(menu);
        log.info("Menu created for location: {}", locationId);
        return saved;
    }

    /**
     * Custom exception for menu not found
     */
    public static class MenuNotFoundException extends RuntimeException {
        public MenuNotFoundException(String message) {
            super(message);
        }
    }
}
