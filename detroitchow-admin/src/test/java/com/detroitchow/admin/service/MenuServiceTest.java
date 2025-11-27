package com.detroitchow.admin.service;

import com.detroitchow.admin.entity.Menu;
import com.detroitchow.admin.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void getMenusByLocationId_WithExistingLocation_ShouldReturnMenusOrderedByPriority() {
        // Given: loc-002 has 2 menus with priorities 1 and 2 (loaded from data-test.sql)

        // When
        List<Menu> menus = menuService.getMenusByLocationId("loc-002");

        // Then
        assertNotNull(menus);
        assertEquals(2, menus.size());
        assertEquals("Pizza Menu", menus.get(0).getDescr());
        assertEquals(1, menus.get(0).getPriority());
        assertEquals("Appetizers & Salads", menus.get(1).getDescr());
        assertEquals(2, menus.get(1).getPriority());
    }

    @Test
    void getMenusByLocationId_WithLocationHavingOneMenu_ShouldReturnSingleMenu() {
        // Given: loc-001 has 1 menu (loaded from data-test.sql)

        // When
        List<Menu> menus = menuService.getMenusByLocationId("loc-001");

        // Then
        assertNotNull(menus);
        assertEquals(1, menus.size());
        assertEquals("Full Menu", menus.get(0).getDescr());
        assertEquals("https://www.coneydetroit.com/menu.pdf", menus.get(0).getMenuLink());
        assertEquals(1, menus.get(0).getPriority());
    }

    @Test
    void getMenusByLocationId_WithLocationHavingNoMenus_ShouldReturnEmptyList() {
        // Given: loc-008 is a location with no menus (loaded from data-test.sql)

        // When
        List<Menu> menus = menuService.getMenusByLocationId("loc-008");

        // Then
        assertNotNull(menus);
        assertTrue(menus.isEmpty());
    }

    @Test
    void getMenusByLocationId_WithNonExistentLocation_ShouldThrowException() {
        // When & Then
        assertThrows(MenuService.MenuNotFoundException.class, () -> {
            menuService.getMenusByLocationId("non-existent-location");
        });
    }

    @Test
    void createMenu_WithValidLocationAndExplicitPriority_ShouldCreateMenu() {
        // Given
        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/new-menu.pdf")
                .descr("New Test Menu")
                .priority(99)
                .image("https://example.com/image.jpg")
                .build();

        // When
        Menu created = menuService.createMenu("loc-001", newMenu);

        // Then
        assertNotNull(created);
        assertNotNull(created.getMenuLink());
        assertEquals("https://example.com/new-menu.pdf", created.getMenuLink());
        assertEquals("New Test Menu", created.getDescr());
        assertEquals(99, created.getPriority());
        assertEquals("https://example.com/image.jpg", created.getImage());
        assertNotNull(created.getCreateDate());
        assertNotNull(created.getUpdatedDate());

        // Verify the location relationship was set
        assertNotNull(created.getLocation());
        assertEquals("loc-001", created.getLocation().getLocationid());

        // Verify it was saved to database
        List<Menu> menus = menuRepository.findByLocationidOrderByPriority("loc-001");
        assertTrue(menus.stream().anyMatch(m -> m.getDescr() != null && m.getDescr().equals("New Test Menu")));
    }

    @Test
    void createMenu_WithoutPriority_ShouldAutoAssignPriorityForLocationWithExistingMenus() {
        // Given: loc-002 has 2 existing menus (priorities 1 and 2)
        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/dessert-menu.pdf")
                .descr("Dessert Menu")
                .build();

        // When
        Menu created = menuService.createMenu("loc-002", newMenu);

        // Then
        assertNotNull(created);
        assertEquals(2, created.getPriority()); // Should be the count of existing menus
        assertEquals("Dessert Menu", created.getDescr());

        // Verify all menus are in correct order
        List<Menu> menus = menuRepository.findByLocationidOrderByPriority("loc-002");
        assertEquals(3, menus.size());
    }

    @Test
    void createMenu_WithoutPriority_ShouldAssignZeroForLocationWithNoMenus() {
        // Given: loc-008 has no existing menus
        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/first-menu.pdf")
                .descr("First Menu")
                .build();

        // When
        Menu created = menuService.createMenu("loc-008", newMenu);

        // Then
        assertNotNull(created);
        assertEquals(0, created.getPriority()); // Should be 0 for first menu
        assertEquals("First Menu", created.getDescr());

        // Verify it was saved
        List<Menu> menus = menuRepository.findByLocationidOrderByPriority("loc-008");
        assertEquals(1, menus.size());
        assertEquals(0, menus.get(0).getPriority());
    }

    @Test
    void createMenu_WithNonExistentLocation_ShouldThrowException() {
        // Given
        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Test Menu")
                .build();

        // When & Then
        assertThrows(MenuService.MenuNotFoundException.class, () -> {
            menuService.createMenu("non-existent-location", newMenu);
        });
    }

    @Test
    void createMenu_WithMinimalData_ShouldCreateMenuWithRequiredFieldsOnly() {
        // Given
        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/simple-menu.pdf")
                .build();

        // When
        Menu created = menuService.createMenu("loc-001", newMenu);

        // Then
        assertNotNull(created);
        assertEquals("https://example.com/simple-menu.pdf", created.getMenuLink());
        assertNull(created.getDescr());
        assertNull(created.getImage());
        assertNotNull(created.getPriority()); // Auto-assigned
        assertNotNull(created.getCreateDate());
        assertNotNull(created.getUpdatedDate());
    }

    @Test
    void getMenusByLocationId_VerifyMenusAreOrderedByPriority() {
        // Given: loc-003 has 2 menus with priorities 1 and 2

        // When
        List<Menu> menus = menuService.getMenusByLocationId("loc-003");

        // Then
        assertEquals(2, menus.size());
        assertEquals(1, menus.get(0).getPriority());
        assertEquals("Dinner Menu", menus.get(0).getDescr());
        assertEquals(2, menus.get(1).getPriority());
        assertEquals("Cocktail Menu", menus.get(1).getDescr());
    }

    @Test
    void getMenusByLocationId_WithLocationHavingManyMenus_ShouldReturnAllInOrder() {
        // Given: loc-007 has 2 menus

        // When
        List<Menu> menus = menuService.getMenusByLocationId("loc-007");

        // Then
        assertEquals(2, menus.size());
        // Verify ordering
        for (int i = 0; i < menus.size() - 1; i++) {
            assertTrue(menus.get(i).getPriority() <= menus.get(i + 1).getPriority(),
                    "Menus should be ordered by priority");
        }
    }

    @Test
    void createMenu_SetsTimestampsCorrectly() {
        // Given
        Menu newMenu = Menu.builder()
                .menuLink("https://example.com/timestamp-test.pdf")
                .descr("Timestamp Test Menu")
                .build();

        // When
        Menu created = menuService.createMenu("loc-001", newMenu);

        // Then
        assertNotNull(created.getCreateDate());
        assertNotNull(created.getUpdatedDate());
        // Create date and update date should be very close (within a few seconds)
        assertTrue(Math.abs(created.getCreateDate().toEpochSecond() -
                           created.getUpdatedDate().toEpochSecond()) < 5);
    }

    @Test
    void menuNotFoundException_ShouldHaveCorrectMessage() {
        // When
        Exception exception = assertThrows(MenuService.MenuNotFoundException.class, () -> {
            menuService.getMenusByLocationId("invalid-location");
        });

        // Then
        assertTrue(exception.getMessage().contains("Location not found"));
        assertTrue(exception.getMessage().contains("invalid-location"));
    }

    @Test
    void createMenu_WithMultipleMenus_ShouldMaintainCorrectPrioritySequence() {
        // Given: loc-001 has 1 menu already
        Menu menu1 = Menu.builder()
                .menuLink("https://example.com/menu-seq-1.pdf")
                .descr("Menu Sequence 1")
                .build();

        Menu menu2 = Menu.builder()
                .menuLink("https://example.com/menu-seq-2.pdf")
                .descr("Menu Sequence 2")
                .build();

        // When
        Menu created1 = menuService.createMenu("loc-001", menu1);
        Menu created2 = menuService.createMenu("loc-001", menu2);

        // Then
        assertEquals(1, created1.getPriority()); // Existing menu count was 1
        assertEquals(2, created2.getPriority()); // Existing menu count was 2

        // Verify all menus are in database
        List<Menu> allMenus = menuRepository.findByLocationidOrderByPriority("loc-001");
        assertEquals(3, allMenus.size()); // Original 1 + 2 new ones
    }

    @Test
    void getMenusByLocationId_WithLocationHavingTwoMenus_ShouldReturnBothInOrder() {
        // Given: loc-005 has 2 menus

        // When
        List<Menu> menus = menuService.getMenusByLocationId("loc-005");

        // Then
        assertEquals(2, menus.size());
        assertEquals("Sliders Menu", menus.get(0).getDescr());
        assertEquals(1, menus.get(0).getPriority());
        assertEquals("Drinks", menus.get(1).getDescr());
        assertEquals(2, menus.get(1).getPriority());
    }
}
