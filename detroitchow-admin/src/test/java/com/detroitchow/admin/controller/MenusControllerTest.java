package com.detroitchow.admin.controller;

import com.detroitchow.admin.dto.MenuDto;
import com.detroitchow.admin.entity.Menu;
import com.detroitchow.admin.mapper.MenuMapper;
import com.detroitchow.admin.service.MenuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenusController.class)
@DisplayName("MenusController Tests")
class MenusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    @MockBean
    private MenuMapper menuMapper;

    private Menu testMenu1;
    private Menu testMenu2;
    private MenuDto testMenuDto1;
    private MenuDto testMenuDto2;

    @BeforeEach
    void setUp() {
        OffsetDateTime now = OffsetDateTime.now();

        testMenu1 = Menu.builder()
                .menuLink("https://example.com/menu1.pdf")
                .descr("Lunch Menu")
                .priority(0)
                .image("https://example.com/menu1.jpg")
                .createDate(now)
                .updatedDate(now)
                .build();

        testMenu2 = Menu.builder()
                .menuLink("https://example.com/menu2.pdf")
                .descr("Dinner Menu")
                .priority(1)
                .image("https://example.com/menu2.jpg")
                .createDate(now)
                .updatedDate(now)
                .build();

        testMenuDto1 = MenuDto.builder()
                .menuLink("https://example.com/menu1.pdf")
                .descr("Lunch Menu")
                .priority(0)
                .image("https://example.com/menu1.jpg")
                .createDate(now)
                .updatedDate(now)
                .build();

        testMenuDto2 = MenuDto.builder()
                .menuLink("https://example.com/menu2.pdf")
                .descr("Dinner Menu")
                .priority(1)
                .image("https://example.com/menu2.jpg")
                .createDate(now)
                .updatedDate(now)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/location/{locationId}/menus - Should return all menus for a location")
    void testGetLocationMenus_Success() throws Exception {
        // Given
        String locationId = "loc-001";
        List<Menu> menus = Arrays.asList(testMenu1, testMenu2);

        when(menuService.getMenusByLocationId(locationId)).thenReturn(menus);
        when(menuMapper.toDto(testMenu1)).thenReturn(testMenuDto1);
        when(menuMapper.toDto(testMenu2)).thenReturn(testMenuDto2);

        // When & Then
        mockMvc.perform(get("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].menuLink").value("https://example.com/menu1.pdf"))
                .andExpect(jsonPath("$.data[0].descr").value("Lunch Menu"))
                .andExpect(jsonPath("$.data[0].priority").value(0))
                .andExpect(jsonPath("$.data[0].image").value("https://example.com/menu1.jpg"))
                .andExpect(jsonPath("$.data[1].menuLink").value("https://example.com/menu2.pdf"))
                .andExpect(jsonPath("$.data[1].descr").value("Dinner Menu"))
                .andExpect(jsonPath("$.data[1].priority").value(1))
                .andExpect(jsonPath("$.data[1].image").value("https://example.com/menu2.jpg"));

        verify(menuService, times(1)).getMenusByLocationId(locationId);
        verify(menuMapper, times(2)).toDto(any(Menu.class));
    }

    @Test
    @DisplayName("GET /api/v1/location/{locationId}/menus - Should return empty list when location has no menus")
    void testGetLocationMenus_EmptyList() throws Exception {
        // Given
        String locationId = "loc-001";
        when(menuService.getMenusByLocationId(locationId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.data", is(empty())));

        verify(menuService, times(1)).getMenusByLocationId(locationId);
        verify(menuMapper, never()).toDto(any(Menu.class));
    }

    @Test
    @DisplayName("GET /api/v1/location/{locationId}/menus - Should return 404 when location not found")
    void testGetLocationMenus_LocationNotFound() throws Exception {
        // Given
        String locationId = "nonexistent-location";
        when(menuService.getMenusByLocationId(locationId))
                .thenThrow(new MenuService.MenuNotFoundException("Location not found: " + locationId));

        // When & Then
        mockMvc.perform(get("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(menuService, times(1)).getMenusByLocationId(locationId);
        verify(menuMapper, never()).toDto(any(Menu.class));
    }

    @Test
    @DisplayName("GET /api/v1/location/{locationId}/menus - Should handle single menu")
    void testGetLocationMenus_SingleMenu() throws Exception {
        // Given
        String locationId = "loc-001";
        List<Menu> menus = Collections.singletonList(testMenu1);

        when(menuService.getMenusByLocationId(locationId)).thenReturn(menus);
        when(menuMapper.toDto(testMenu1)).thenReturn(testMenuDto1);

        // When & Then
        mockMvc.perform(get("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].menuLink").value("https://example.com/menu1.pdf"))
                .andExpect(jsonPath("$.data[0].descr").value("Lunch Menu"));

        verify(menuService, times(1)).getMenusByLocationId(locationId);
        verify(menuMapper, times(1)).toDto(testMenu1);
    }

    @Test
    @DisplayName("POST /api/v1/location/{locationId}/menus - Should create menu successfully")
    void testAddMenuToLocation_Success() throws Exception {
        // Given
        String locationId = "loc-001";
        MenuDto newMenuDto = MenuDto.builder()
                .menuLink("https://example.com/new-menu.pdf")
                .descr("Breakfast Menu")
                .image("https://example.com/breakfast.jpg")
                .build();

        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/new-menu.pdf")
                .descr("Breakfast Menu")
                .image("https://example.com/breakfast.jpg")
                .build();

        Menu savedMenu = Menu.builder()
                .menuLink("https://example.com/new-menu.pdf")
                .descr("Breakfast Menu")
                .priority(0)
                .image("https://example.com/breakfast.jpg")
                .createDate(OffsetDateTime.now())
                .updatedDate(OffsetDateTime.now())
                .build();

        MenuDto savedMenuDto = MenuDto.builder()
                .menuLink("https://example.com/new-menu.pdf")
                .descr("Breakfast Menu")
                .priority(0)
                .image("https://example.com/breakfast.jpg")
                .createDate(savedMenu.getCreateDate())
                .updatedDate(savedMenu.getUpdatedDate())
                .build();

        when(menuMapper.toEntity(any(MenuDto.class))).thenReturn(newMenu);
        when(menuService.createMenu(eq(locationId), any(Menu.class))).thenReturn(savedMenu);
        when(menuMapper.toDto(savedMenu)).thenReturn(savedMenuDto);

        // When & Then
        mockMvc.perform(post("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMenuDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.menuLink").value("https://example.com/new-menu.pdf"))
                .andExpect(jsonPath("$.data.descr").value("Breakfast Menu"))
                .andExpect(jsonPath("$.data.priority").value(0))
                .andExpect(jsonPath("$.data.image").value("https://example.com/breakfast.jpg"))
                .andExpect(jsonPath("$.message").value("Menu added successfully"));

        verify(menuMapper, times(1)).toEntity(any(MenuDto.class));
        verify(menuService, times(1)).createMenu(eq(locationId), any(Menu.class));
        verify(menuMapper, times(1)).toDto(savedMenu);
    }

    @Test
    @DisplayName("POST /api/v1/location/{locationId}/menus - Should create menu with minimal fields")
    void testAddMenuToLocation_MinimalFields() throws Exception {
        // Given
        String locationId = "loc-001";
        MenuDto minimalMenuDto = MenuDto.builder()
                .menuLink("https://example.com/minimal-menu.pdf")
                .build();

        Menu minimalMenu = Menu.builder()
                .menuLink("https://example.com/minimal-menu.pdf")
                .build();

        Menu savedMenu = Menu.builder()
                .menuLink("https://example.com/minimal-menu.pdf")
                .priority(0)
                .createDate(OffsetDateTime.now())
                .updatedDate(OffsetDateTime.now())
                .build();

        MenuDto savedMenuDto = MenuDto.builder()
                .menuLink("https://example.com/minimal-menu.pdf")
                .priority(0)
                .build();

        when(menuMapper.toEntity(any(MenuDto.class))).thenReturn(minimalMenu);
        when(menuService.createMenu(eq(locationId), any(Menu.class))).thenReturn(savedMenu);
        when(menuMapper.toDto(savedMenu)).thenReturn(savedMenuDto);

        // When & Then
        mockMvc.perform(post("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalMenuDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.menuLink").value("https://example.com/minimal-menu.pdf"))
                .andExpect(jsonPath("$.data.priority").value(0))
                .andExpect(jsonPath("$.message").value("Menu added successfully"));

        verify(menuService, times(1)).createMenu(eq(locationId), any(Menu.class));
    }

    @Test
    @DisplayName("POST /api/v1/location/{locationId}/menus - Should return 404 when location not found")
    void testAddMenuToLocation_LocationNotFound() throws Exception {
        // Given
        String locationId = "nonexistent-location";
        MenuDto newMenuDto = MenuDto.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Test Menu")
                .build();

        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Test Menu")
                .build();

        when(menuMapper.toEntity(any(MenuDto.class))).thenReturn(newMenu);
        when(menuService.createMenu(eq(locationId), any(Menu.class)))
                .thenThrow(new MenuService.MenuNotFoundException("Location not found: " + locationId));

        // When & Then
        mockMvc.perform(post("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMenuDto)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(menuMapper, times(1)).toEntity(any(MenuDto.class));
        verify(menuService, times(1)).createMenu(eq(locationId), any(Menu.class));
        verify(menuMapper, never()).toDto(any(Menu.class));
    }

    @Test
    @DisplayName("POST /api/v1/location/{locationId}/menus - Should create menu with all fields")
    void testAddMenuToLocation_AllFields() throws Exception {
        // Given
        String locationId = "loc-001";
        MenuDto fullMenuDto = MenuDto.builder()
                .menuLink("https://example.com/full-menu.pdf")
                .descr("Complete Seasonal Menu")
                .priority(5)
                .image("https://example.com/full-menu.jpg")
                .build();

        Menu fullMenu = Menu.builder()
                .menuLink("https://example.com/full-menu.pdf")
                .descr("Complete Seasonal Menu")
                .priority(5)
                .image("https://example.com/full-menu.jpg")
                .build();

        Menu savedMenu = Menu.builder()
                .menuLink("https://example.com/full-menu.pdf")
                .descr("Complete Seasonal Menu")
                .priority(5)
                .image("https://example.com/full-menu.jpg")
                .createDate(OffsetDateTime.now())
                .updatedDate(OffsetDateTime.now())
                .build();

        MenuDto savedMenuDto = MenuDto.builder()
                .menuLink("https://example.com/full-menu.pdf")
                .descr("Complete Seasonal Menu")
                .priority(5)
                .image("https://example.com/full-menu.jpg")
                .build();

        when(menuMapper.toEntity(any(MenuDto.class))).thenReturn(fullMenu);
        when(menuService.createMenu(eq(locationId), any(Menu.class))).thenReturn(savedMenu);
        when(menuMapper.toDto(savedMenu)).thenReturn(savedMenuDto);

        // When & Then
        mockMvc.perform(post("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullMenuDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.menuLink").value("https://example.com/full-menu.pdf"))
                .andExpect(jsonPath("$.data.descr").value("Complete Seasonal Menu"))
                .andExpect(jsonPath("$.data.priority").value(5))
                .andExpect(jsonPath("$.data.image").value("https://example.com/full-menu.jpg"))
                .andExpect(jsonPath("$.message").value("Menu added successfully"));

        verify(menuService, times(1)).createMenu(eq(locationId), any(Menu.class));
    }

    @Test
    @DisplayName("GET /api/v1/location/{locationId}/menus - Should handle OSM-style location IDs")
    void testGetLocationMenus_OsmLocationId() throws Exception {
        // Given
        String osmLocationId = "osm-n123456";
        List<Menu> menus = Collections.singletonList(testMenu1);

        when(menuService.getMenusByLocationId(osmLocationId)).thenReturn(menus);
        when(menuMapper.toDto(testMenu1)).thenReturn(testMenuDto1);

        // When & Then
        mockMvc.perform(get("/api/v1/location/{locationId}/menus", osmLocationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].menuLink").value("https://example.com/menu1.pdf"));

        verify(menuService, times(1)).getMenusByLocationId(osmLocationId);
    }

    @Test
    @DisplayName("POST /api/v1/location/{locationId}/menus - Should handle OSM-style location IDs")
    void testAddMenuToLocation_OsmLocationId() throws Exception {
        // Given
        String osmLocationId = "osm-n123456";
        MenuDto newMenuDto = MenuDto.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Test Menu")
                .build();

        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Test Menu")
                .build();

        Menu savedMenu = Menu.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Test Menu")
                .priority(0)
                .createDate(OffsetDateTime.now())
                .updatedDate(OffsetDateTime.now())
                .build();

        MenuDto savedMenuDto = MenuDto.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Test Menu")
                .priority(0)
                .build();

        when(menuMapper.toEntity(any(MenuDto.class))).thenReturn(newMenu);
        when(menuService.createMenu(eq(osmLocationId), any(Menu.class))).thenReturn(savedMenu);
        when(menuMapper.toDto(savedMenu)).thenReturn(savedMenuDto);

        // When & Then
        mockMvc.perform(post("/api/v1/location/{locationId}/menus", osmLocationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMenuDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.menuLink").value("https://example.com/menu.pdf"))
                .andExpect(jsonPath("$.message").value("Menu added successfully"));

        verify(menuService, times(1)).createMenu(eq(osmLocationId), any(Menu.class));
    }

    @Test
    @DisplayName("GET /api/v1/location/{locationId}/menus - Should return menus ordered by priority")
    void testGetLocationMenus_OrderedByPriority() throws Exception {
        // Given
        String locationId = "loc-001";

        Menu menu1 = Menu.builder()
                .menuLink("https://example.com/menu1.pdf")
                .descr("Third Priority")
                .priority(2)
                .build();

        Menu menu2 = Menu.builder()
                .menuLink("https://example.com/menu2.pdf")
                .descr("First Priority")
                .priority(0)
                .build();

        Menu menu3 = Menu.builder()
                .menuLink("https://example.com/menu3.pdf")
                .descr("Second Priority")
                .priority(1)
                .build();

        MenuDto menuDto1 = MenuDto.builder()
                .menuLink("https://example.com/menu1.pdf")
                .descr("Third Priority")
                .priority(2)
                .build();

        MenuDto menuDto2 = MenuDto.builder()
                .menuLink("https://example.com/menu2.pdf")
                .descr("First Priority")
                .priority(0)
                .build();

        MenuDto menuDto3 = MenuDto.builder()
                .menuLink("https://example.com/menu3.pdf")
                .descr("Second Priority")
                .priority(1)
                .build();

        // Service returns menus ordered by priority
        List<Menu> orderedMenus = Arrays.asList(menu2, menu3, menu1);

        when(menuService.getMenusByLocationId(locationId)).thenReturn(orderedMenus);
        when(menuMapper.toDto(menu2)).thenReturn(menuDto2);
        when(menuMapper.toDto(menu3)).thenReturn(menuDto3);
        when(menuMapper.toDto(menu1)).thenReturn(menuDto1);

        // When & Then
        mockMvc.perform(get("/api/v1/location/{locationId}/menus", locationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].priority").value(0))
                .andExpect(jsonPath("$.data[0].descr").value("First Priority"))
                .andExpect(jsonPath("$.data[1].priority").value(1))
                .andExpect(jsonPath("$.data[1].descr").value("Second Priority"))
                .andExpect(jsonPath("$.data[2].priority").value(2))
                .andExpect(jsonPath("$.data[2].descr").value("Third Priority"));

        verify(menuService, times(1)).getMenusByLocationId(locationId);
    }
}
