package ie.cct.fastfood.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ie.cct.fastfood.datastructure.CircularDequeStorage;
import ie.cct.fastfood.datastructure.StorageEmptyException;
import ie.cct.fastfood.datastructure.StorageFullException;
import ie.cct.fastfood.datastructure.StorageMode;
import ie.cct.fastfood.model.FoodItem;
import ie.cct.fastfood.model.Pizza;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the full add -> store -> search flow, the same path the GUI uses. */
public class StorageManagerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 20, 12, 30, 0);
    private StorageManager manager;

    @BeforeEach
    public void setUp() {
        ZoneId zone = ZoneId.of("Europe/Dublin");
        Clock clock = Clock.fixed(NOW.atZone(zone).toInstant(), zone);
        manager = new StorageManager(new CircularDequeStorage<>(StorageMode.STACK), new FoodValidator(clock), clock);
    }

    @Test
    public void addedItemKeepsAllDetailsAndPlacedTime() throws Exception {
        FoodItem item = manager.addFood("pizza", "800", "25/10/2026");
        assertTrue(item instanceof Pizza);
        assertEquals("Pizza", item.getName());
        assertEquals(800, item.getWeightGrams());
        assertEquals(NOW, item.getPlacedAt());
        assertEquals(1, manager.size());
    }

    @Test
    public void invalidInputDoesNotChangeTheStorage() {
        assertThrows(ValidationException.class, () -> manager.addFood("Pasta", "200", "25/10/2026"));
        assertThrows(ValidationException.class, () -> manager.addFood("Fries", "lots", "25/10/2026"));
        assertThrows(ValidationException.class, () -> manager.addFood("Fries", "200", "01/01/2020"));
        assertEquals(0, manager.size());
    }

    @Test
    public void fullStorageIsReportedBeforeValidation() throws Exception {
        for (int i = 0; i < 8; i++) {
            manager.addFood("Burger", "200", "25/10/2026");
        }
        assertThrows(StorageFullException.class, () -> manager.addFood("Burger", "200", "25/10/2026"));
        // even with bad input, the "full" message is what the user should see
        assertThrows(StorageFullException.class, () -> manager.addFood("???", "", ""));
    }

    @Test
    public void emptyStorageIsHandledForEveryOperation() {
        assertThrows(StorageEmptyException.class, manager::removeFood);
        assertThrows(StorageEmptyException.class, manager::peekNext);
        assertThrows(StorageEmptyException.class, manager::displayAll);
        assertThrows(StorageEmptyException.class, () -> manager.searchByName("Pizza"));
        assertThrows(StorageEmptyException.class, () -> manager.searchByWeight("1", "100"));
        assertThrows(StorageEmptyException.class, () -> manager.searchByBestBefore("20/10/2026", "30/10/2026"));
    }

    @Test
    public void searchByNameReturnsOnlyThatFood() throws Exception {
        manager.addFood("Pizza", "800", "25/10/2026");
        manager.addFood("Fries", "150", "22/10/2026");
        manager.addFood("Pizza", "600", "27/10/2026");

        List<FoodItem> pizzas = manager.searchByName("PIZZA");
        assertEquals(2, pizzas.size());
        assertTrue(manager.searchByName("Hotdog").isEmpty());
    }

    @Test
    public void searchByWeightAndDateRange() throws Exception {
        manager.addFood("Pizza", "800", "25/10/2026");
        manager.addFood("Fries", "150", "22/10/2026");
        manager.addFood("Sandwich", "300", "30/10/2026");

        assertEquals(2, manager.searchByWeight("100", "400").size());
        assertEquals(1, manager.searchByBestBefore("20/10/2026", "23/10/2026").size());
        assertEquals(3, manager.searchByBestBefore("20/10/2026", "03/11/2026").size());
    }

    @Test
    public void removeFollowsTheChosenMode() throws Exception {
        manager.addFood("Burger", "200", "25/10/2026");
        manager.addFood("Pizza", "800", "25/10/2026");

        assertEquals("Pizza", manager.peekNext().getName());   // stack: last in
        manager.setMode(StorageMode.QUEUE);
        assertEquals("Burger", manager.peekNext().getName());  // queue: first in
        assertEquals("Burger", manager.removeFood().getName());
        assertEquals(1, manager.size());
    }
}