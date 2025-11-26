package com.detroitchow.admin.controller;

import com.detroitchow.admin.entity.Menu;
import com.detroitchow.admin.service.MenuService;
import com.detroitchow.admin.dto.MenuDto;
import com.detroitchow.admin.mapper.MenuMapper;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class MenusController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;

    /**
     * GET /location/{locationId}/menus - Get all menus for a location
     */
    @GetMapping("/location/{locationId}/menus")
    public ResponseEntity<Map<String, Object>> getLocationMenus(@PathVariable String locationId) {
        log.debug("Getting menus for location: {}", locationId);
        
        try {
            List<Menu> menus = menuService.getMenusByLocationId(locationId);
            
            List<MenuDto> menuDtos = menus.stream()
                    .map(menuMapper::toDto)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", menuDtos);
            
            return ResponseEntity.ok(response);
        } catch (MenuService.MenuNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /location/{locationId}/menus - Add a new menu to a location
     */
    @PostMapping("/location/{locationId}/menus")
    public ResponseEntity<Map<String, Object>> addMenuToLocation(
            @PathVariable String locationId,
            @RequestBody MenuDto menuDto) {
        
        log.debug("Adding menu to location: {}", locationId);
        
        try {
            Menu menu = menuMapper.toEntity(menuDto);
            Menu saved = menuService.createMenu(locationId, menu);
            
            MenuDto savedDto = menuMapper.toDto(saved);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", savedDto);
            response.put("message", "Menu added successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (MenuService.MenuNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /location/{locationId}/menus/{menuId} - Get a specific menu
     */
    @GetMapping("/location/{locationId}/menus/{menuId}")
    public ResponseEntity<?> getMenu(@PathVariable String locationId, @PathVariable String menuId) {
        log.debug("Getting menu {} for location: {}", menuId, locationId);
        
        return menuService.getMenuByLocationIdAndMenuLink(locationId, menuId)
                .map(menu -> {
                    MenuDto dto = menuMapper.toDto(menu);
                    Map<String, Object> response = new HashMap<>();
                    response.put("data", dto);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /location/{locationId}/menus/{menuId} - Update a menu
     */
    @PutMapping("/location/{locationId}/menus/{menuId}")
    public ResponseEntity<?> updateMenu(
            @PathVariable String locationId,
            @PathVariable String menuId,
            @RequestBody MenuDto menuDto) {
        
        log.debug("Updating menu {} for location: {}", menuId, locationId);
        
        try {
            Menu menu = menuMapper.toEntity(menuDto);
            Menu updated = menuService.updateMenu(locationId, menuId, menu);
            
            MenuDto updatedDto = menuMapper.toDto(updated);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", updatedDto);
            response.put("message", "Menu updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (MenuService.MenuNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /location/{locationId}/menus/{menuId} - Delete a menu
     */
    @DeleteMapping("/location/{locationId}/menus/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable String locationId, @PathVariable String menuId) {
        log.debug("Deleting menu {} for location: {}", menuId, locationId);
        
        try {
            menuService.deleteMenu(locationId, menuId);
            return ResponseEntity.noContent().build();
        } catch (MenuService.MenuNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /location/{locationId}/menus/reorder - Reorder menus by priority
     */
    @PostMapping("/location/{locationId}/menus/reorder")
    public ResponseEntity<Map<String, Object>> reorderMenus(
            @PathVariable String locationId,
            @RequestBody Map<String, List<String>> request) {
        
        log.debug("Reordering menus for location: {}", locationId);
        
        try {
            List<String> menuIds = request.get("menuIds");
            List<Menu> reorderedMenus = menuService.reorderMenus(locationId, menuIds);
            
            List<MenuDto> menuDtos = reorderedMenus.stream()
                    .map(menuMapper::toDto)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", menuDtos);
            response.put("message", "Menus reordered successfully");
            
            return ResponseEntity.ok(response);
        } catch (MenuService.MenuNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
