package ie.cct.fastfood.datastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests the data structure on its own, using simple Strings as items. */
public class CircularDequeStorageTest {

    private CircularDequeStorage<String> fill(StorageMode mode, String... items) throws StorageFullException {
        CircularDequeStorage<String> storage = new CircularDequeStorage<>(mode);
        for (String item : items) {
            storage.add(item);
        }
        return storage;
    }

    @Test
    public void newStorageIsEmptyWithCapacityEight() {
        CircularDequeStorage<String> storage = new CircularDequeStorage<>(StorageMode.STACK);
        assertTrue(storage.isEmpty());
        assertFalse(storage.isFull());
        assertEquals(0, storage.size());
        assertEquals(8, storage.capacity());
    }

    @Test
    public void addingNinthItemThrowsStorageFull() throws StorageFullException {
        CircularDequeStorage<String> storage = fill(StorageMode.STACK, "1", "2", "3", "4", "5", "6", "7", "8");
        assertTrue(storage.isFull());
        assertThrows(StorageFullException.class, () -> storage.add("9"));
        assertEquals(8, storage.size());
    }

    @Test
    public void stackModeRemovesLastAddedFirst() throws StorageException {
        CircularDequeStorage<String> storage = fill(StorageMode.STACK, "Burger", "Pizza", "Fries");
        assertEquals("Fries", storage.remove());
        assertEquals("Pizza", storage.remove());
        assertEquals("Burger", storage.remove());
        assertTrue(storage.isEmpty());
    }

    @Test
    public void queueModeRemovesFirstAddedFirst() throws StorageException {
        CircularDequeStorage<String> storage = fill(StorageMode.QUEUE, "Burger", "Pizza", "Fries");
        assertEquals("Burger", storage.remove());
        assertEquals("Pizza", storage.remove());
        assertEquals("Fries", storage.remove());
        assertTrue(storage.isEmpty());
    }

    @Test
    public void peekShowsNextItemWithoutRemovingIt() throws StorageException {
        CircularDequeStorage<String> stack = fill(StorageMode.STACK, "Burger", "Pizza");
        assertEquals("Pizza", stack.peek());
        assertEquals(2, stack.size());

        CircularDequeStorage<String> queue = fill(StorageMode.QUEUE, "Burger", "Pizza");
        assertEquals("Burger", queue.peek());
        assertEquals("Pizza", queue.peekFront());
        assertEquals(2, queue.size());
    }

    @Test
    public void removeAndPeekOnEmptyStorageThrow() {
        CircularDequeStorage<String> storage = new CircularDequeStorage<>(StorageMode.QUEUE);
        assertThrows(StorageEmptyException.class, storage::remove);
        assertThrows(StorageEmptyException.class, storage::peek);
        assertThrows(StorageEmptyException.class, storage::peekFront);
        assertThrows(StorageEmptyException.class, () -> storage.search(s -> true));
    }

    @Test
    public void toListIsOrderedFrontToBack() throws StorageFullException {
        CircularDequeStorage<String> storage = fill(StorageMode.QUEUE, "A", "B", "C");
        // C was added last so it is at the front
        assertEquals(List.of("C", "B", "A"), storage.toList());
    }

    @Test
    public void queueKeepsCorrectOrderAfterWrappingAround() throws StorageException {
        // Adding and removing many more items than the capacity forces the
        // front index to wrap around the array several times.
        CircularDequeStorage<Integer> storage = new CircularDequeStorage<>(StorageMode.QUEUE);
        List<Integer> removed = new ArrayList<>();
        int next = 0;
        for (int round = 0; round < 5; round++) {
            while (!storage.isFull()) {
                storage.add(next++);
            }
            for (int i = 0; i < 5; i++) {
                removed.add(storage.remove());
            }
        }
        while (!storage.isEmpty()) {
            removed.add(storage.remove());
        }
        for (int i = 0; i < removed.size(); i++) {
            assertEquals(i, removed.get(i), "items should leave in the same order they arrived");
        }
    }

    @Test
    public void switchingModeChangesWhichSideIsRemoved() throws StorageException {
        CircularDequeStorage<String> storage = fill(StorageMode.STACK, "A", "B", "C");
        storage.setMode(StorageMode.QUEUE);
        assertEquals("A", storage.remove());
        storage.setMode(StorageMode.STACK);
        assertEquals("C", storage.remove());
        assertEquals(List.of("B"), storage.toList());
    }

    @Test
    public void searchReturnsAllMatches() throws StorageException {
        CircularDequeStorage<String> storage = fill(StorageMode.STACK, "Pizza", "Fries", "Pizza");
        assertEquals(2, storage.search(s -> s.equals("Pizza")).size());
        assertTrue(storage.search(s -> s.equals("Hotdog")).isEmpty());
    }

    @Test
    public void nullItemIsRejected() {
        CircularDequeStorage<String> storage = new CircularDequeStorage<>(StorageMode.STACK);
        assertThrows(IllegalArgumentException.class, () -> storage.add(null));
    }
}