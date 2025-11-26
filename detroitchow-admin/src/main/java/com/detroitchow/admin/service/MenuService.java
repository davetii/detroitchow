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
     * Get a specific menu by location ID and menu link
     */
    public Optional<Menu> getMenuByLocationIdAndMenuLink(String locationId, String menuLink) {
        return menuRepository.findByLocationidAndMenuLink(locationId, menuLink);
    }

    /**
     * Create a new menu for a location
     */
    public Menu createMenu(String locationId, Menu menu) {
        Optional<Location> location = locationRepository.findById(locationId);
        if (location.isEmpty()) {
            throw new MenuNotFoundException("Location not found: " + locationId);
        }

        menu.setLocationid(locationId);
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
     * Update an existing menu
     */
    public Menu updateMenu(String locationId, String menuLink, Menu menu) {
        Optional<Menu> existing = menuRepository.findByLocationidAndMenuLink(locationId, menuLink);
        
        if (existing.isEmpty()) {
            throw new MenuNotFoundException("Menu not found for location: " + locationId + ", menu: " + menuLink);
        }

        Menu existingMenu = existing.get();
        
        // Update fields
        if (menu.getDescr() != null) {
            existingMenu.setDescr(menu.getDescr());
        }
        if (menu.getMenuLink() != null && !menu.getMenuLink().equals(menuLink)) {
            existingMenu.setMenuLink(menu.getMenuLink());
        }
        if (menu.getPriority() != null) {
            existingMenu.setPriority(menu.getPriority());
        }
        if (menu.getImage() != null) {
            existingMenu.setImage(menu.getImage());
        }
        
        existingMenu.setUpdatedDate(OffsetDateTime.now());

        Menu updated = menuRepository.save(existingMenu);
        log.info("Menu updated for location: {}", locationId);
        return updated;
    }

    /**
     * Delete a menu
     */
    public void deleteMenu(String locationId, String menuLink) {
        Optional<Menu> menu = menuRepository.findByLocationidAndMenuLink(locationId, menuLink);
        
        if (menu.isEmpty()) {
            throw new MenuNotFoundException("Menu not found for location: " + locationId + ", menu: " + menuLink);
        }
        
        menuRepository.delete(menu.get());
        log.info("Menu deleted for location: {}", locationId);
    }

    /**
     * Reorder menus by setting new priorities
     * The list order represents the desired priority (first item = priority 0)
     */
    public List<Menu> reorderMenus(String locationId, List<String> menuLinks) {
        List<Menu> menus = menuRepository.findByLocationidOrderByPriority(locationId);
        
        if (menus.isEmpty()) {
            throw new MenuNotFoundException("No menus found for location: " + locationId);
        }

        // Update priorities based on the provided order
        for (int i = 0; i < menuLinks.size(); i++) {
            String menuLink = menuLinks.get(i);
            Optional<Menu> menu = menus.stream()
                    .filter(m -> m.getMenuLink().equals(menuLink))
                    .findFirst();
            
            if (menu.isPresent()) {
                menu.get().setPriority(i);
                menu.get().setUpdatedDate(OffsetDateTime.now());
                menuRepository.save(menu.get());
            }
        }

        log.info("Menus reordered for location: {}", locationId);
        return menuRepository.findByLocationidOrderByPriority(locationId);
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
