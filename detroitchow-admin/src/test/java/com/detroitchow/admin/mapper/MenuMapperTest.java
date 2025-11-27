package com.detroitchow.admin.mapper;

import com.detroitchow.admin.dto.MenuDto;
import com.detroitchow.admin.entity.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MenuMapperTest {

    private MenuMapper menuMapper;

    @BeforeEach
    void setUp() {
        menuMapper = new MenuMapper();
    }

    @Test
    void testToDto_WithFullMenu_ShouldMapAllFields() {
        // Given
        OffsetDateTime createDate = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedDate = OffsetDateTime.now();

        Menu menu = Menu.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Dinner Menu")
                .priority(1)
                .image("https://example.com/menu.jpg")
                .createDate(createDate)
                .createUser("admin")
                .updatedDate(updatedDate)
                .updateUser("admin2")
                .build();

        // When
        MenuDto dto = menuMapper.toDto(menu);

        // Then
        assertNotNull(dto);
        assertEquals("https://example.com/menu.pdf", dto.getMenuLink());
        assertEquals("Dinner Menu", dto.getDescr());
        assertEquals(1, dto.getPriority());
        assertEquals("https://example.com/menu.jpg", dto.getImage());
        assertEquals(createDate, dto.getCreateDate());
        assertEquals("admin", dto.getCreateUser());
        assertEquals(updatedDate, dto.getUpdatedDate());
        assertEquals("admin2", dto.getUpdateUser());
    }

    @Test
    void testToDto_WithNullMenu_ShouldReturnNull() {
        // When
        MenuDto dto = menuMapper.toDto(null);

        // Then
        assertNull(dto);
    }

    @Test
    void testToDto_WithMinimalMenu_ShouldMapOnlyProvidedFields() {
        // Given
        Menu menu = Menu.builder()
                .menuLink("https://example.com/menu.pdf")
                .build();

        // When
        MenuDto dto = menuMapper.toDto(menu);

        // Then
        assertNotNull(dto);
        assertEquals("https://example.com/menu.pdf", dto.getMenuLink());
        assertNull(dto.getDescr());
        assertNull(dto.getPriority());
        assertNull(dto.getImage());
        assertNull(dto.getCreateDate());
        assertNull(dto.getCreateUser());
        assertNull(dto.getUpdatedDate());
        assertNull(dto.getUpdateUser());
    }

    @Test
    void testToEntity_WithFullDto_ShouldMapAllFields() {
        // Given
        OffsetDateTime createDate = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedDate = OffsetDateTime.now();

        MenuDto dto = MenuDto.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Lunch Menu")
                .priority(2)
                .image("https://example.com/lunch.jpg")
                .createDate(createDate)
                .createUser("user1")
                .updatedDate(updatedDate)
                .updateUser("user2")
                .build();

        // When
        Menu menu = menuMapper.toEntity(dto);

        // Then
        assertNotNull(menu);
        assertEquals("https://example.com/menu.pdf", menu.getMenuLink());
        assertEquals("Lunch Menu", menu.getDescr());
        assertEquals(2, menu.getPriority());
        assertEquals("https://example.com/lunch.jpg", menu.getImage());
        assertEquals(createDate, menu.getCreateDate());
        assertEquals("user1", menu.getCreateUser());
        assertEquals(updatedDate, menu.getUpdatedDate());
        assertEquals("user2", menu.getUpdateUser());
    }

    @Test
    void testToEntity_WithNullDto_ShouldReturnNull() {
        // When
        Menu menu = menuMapper.toEntity(null);

        // Then
        assertNull(menu);
    }

    @Test
    void testToEntity_WithMinimalDto_ShouldMapOnlyProvidedFields() {
        // Given
        MenuDto dto = MenuDto.builder()
                .menuLink("https://example.com/menu.pdf")
                .descr("Weekend Specials")
                .build();

        // When
        Menu menu = menuMapper.toEntity(dto);

        // Then
        assertNotNull(menu);
        assertEquals("https://example.com/menu.pdf", menu.getMenuLink());
        assertEquals("Weekend Specials", menu.getDescr());
        assertNull(menu.getPriority());
        assertNull(menu.getImage());
        assertNull(menu.getCreateDate());
        assertNull(menu.getCreateUser());
        assertNull(menu.getUpdatedDate());
        assertNull(menu.getUpdateUser());
    }

    @Test
    void testRoundTrip_EntityToDtoToEntity_ShouldPreserveData() {
        // Given
        OffsetDateTime createDate = OffsetDateTime.now().minusDays(2);
        OffsetDateTime updatedDate = OffsetDateTime.now().minusDays(1);

        Menu originalMenu = Menu.builder()
                .menuLink("https://example.com/brunch.pdf")
                .descr("Brunch Menu")
                .priority(3)
                .image("https://example.com/brunch.jpg")
                .createDate(createDate)
                .createUser("chef")
                .updatedDate(updatedDate)
                .updateUser("manager")
                .build();

        // When
        MenuDto dto = menuMapper.toDto(originalMenu);
        Menu resultMenu = menuMapper.toEntity(dto);

        // Then
        assertNotNull(resultMenu);
        assertEquals(originalMenu.getMenuLink(), resultMenu.getMenuLink());
        assertEquals(originalMenu.getDescr(), resultMenu.getDescr());
        assertEquals(originalMenu.getPriority(), resultMenu.getPriority());
        assertEquals(originalMenu.getImage(), resultMenu.getImage());
        assertEquals(originalMenu.getCreateDate(), resultMenu.getCreateDate());
        assertEquals(originalMenu.getCreateUser(), resultMenu.getCreateUser());
        assertEquals(originalMenu.getUpdatedDate(), resultMenu.getUpdatedDate());
        assertEquals(originalMenu.getUpdateUser(), resultMenu.getUpdateUser());
    }

    @Test
    void testRoundTrip_DtoToEntityToDto_ShouldPreserveData() {
        // Given
        OffsetDateTime createDate = OffsetDateTime.now().minusDays(3);
        OffsetDateTime updatedDate = OffsetDateTime.now();

        MenuDto originalDto = MenuDto.builder()
                .menuLink("https://example.com/dessert.pdf")
                .descr("Dessert Menu")
                .priority(5)
                .image("https://example.com/dessert.jpg")
                .createDate(createDate)
                .createUser("pastry_chef")
                .updatedDate(updatedDate)
                .updateUser("supervisor")
                .build();

        // When
        Menu menu = menuMapper.toEntity(originalDto);
        MenuDto resultDto = menuMapper.toDto(menu);

        // Then
        assertNotNull(resultDto);
        assertEquals(originalDto.getMenuLink(), resultDto.getMenuLink());
        assertEquals(originalDto.getDescr(), resultDto.getDescr());
        assertEquals(originalDto.getPriority(), resultDto.getPriority());
        assertEquals(originalDto.getImage(), resultDto.getImage());
        assertEquals(originalDto.getCreateDate(), resultDto.getCreateDate());
        assertEquals(originalDto.getCreateUser(), resultDto.getCreateUser());
        assertEquals(originalDto.getUpdatedDate(), resultDto.getUpdatedDate());
        assertEquals(originalDto.getUpdateUser(), resultDto.getUpdateUser());
    }
}
