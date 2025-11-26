package com.detroitchow.admin.repository;

import com.detroitchow.admin.entity.Menu;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MenuRepositoryTest {
    @Autowired
    MenuRepository repo;

    @Test
    void ensureLocationIdLookupReturnsExpected() {
        List<Menu>  l = repo.findByLocationidOrderByPriority("loc-001");
        assertEquals(1, l.size());
        assertEquals("loc-001", l.get(0).getLocationid());
        assertEquals("https://www.coneydetroit.com/menu.pdf", l.get(0).getMenuLink());
        assertEquals(1, l.get(0).getPriority());
    }

    @Test
    void ensureLocationIdLookupReturnsExpectedWithMultipleRecords() {
        List<Menu>  l = repo.findByLocationidOrderByPriority("loc-002");
        assertEquals(2, l.size());
        assertEquals("loc-002", l.get(0).getLocationid());
        assertEquals("https://www.buddyspizza.com/menu/pizza", l.get(0).getMenuLink());
        assertEquals("Pizza Menu", l.get(0).getDescr());
        assertEquals(1, l.get(0).getPriority());

        assertEquals("loc-002", l.get(1).getLocationid());
        assertEquals("https://www.buddyspizza.com/menu/appetizers", l.get(1).getMenuLink());
        assertEquals("Appetizers & Salads", l.get(1).getDescr());
        assertEquals(2, l.get(1).getPriority());
    }

}
