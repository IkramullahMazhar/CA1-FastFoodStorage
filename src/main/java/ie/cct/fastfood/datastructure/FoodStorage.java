package ie.cct.fastfood.datastructure;

import java.util.List;
import java.util.function.Predicate;

/**
 * The operations our storage unit must support. Written as an interface
 * so the GUI and the manager do not depend on how the storage is built
 * inside (abstraction).
 *
 * @param <T> the type of item kept on the trays
 */
public interface FoodStorage<T> extends Iterable<T> {

    /** Places an item on the front side. O(1) */
    void add(T item) throws StorageFullException;

    /** Takes the next item out, from the side decided by the current mode. O(1) */
    T remove() throws StorageEmptyException;

    /** Looks at the next item that would come out, without removing it. O(1) */
    T peek() throws StorageEmptyException;

    /** Looks at the item most recently placed at the front. O(1) */
    T peekFront() throws StorageEmptyException;

    /** Returns all items in order from FRONT to BACK. O(n) */
    List<T> toList();

    /** Returns every item matching the condition. O(n) linear search */
    List<T> search(Predicate<T> condition) throws StorageEmptyException;

    int size();

    int capacity();

    boolean isEmpty();

    boolean isFull();

    StorageMode getMode();

    void setMode(StorageMode mode);
}