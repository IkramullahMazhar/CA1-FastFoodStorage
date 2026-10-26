package ie.cct.fastfood.datastructure;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * A fixed-size double-ended queue (deque) built on a circular array.
 *
 * Why a deque? The storage unit has two sides. The chef always ADDS at the
 * front, but can REMOVE from the front (Stack / LIFO) or from the back
 * (Queue / FIFO). A deque allows O(1) access at both ends, so one structure
 * supports both ways of working.
 *
 * Why a circular array? The unit physically holds 8 trays, so the size is
 * fixed. An array of 8 slots matches that exactly. Using "wrap around"
 * indexes means we never have to shift items when adding or removing, so
 * every add/remove/peek is constant time.
 *
 * Index layout: {@code front} is the slot of the item at the front side.
 * The item at position i (0 = front) is stored at (front + i) % capacity.
 * The back item is at (front + size - 1) % capacity.
 */
public class CircularDequeStorage<T> implements FoodStorage<T> {

    public static final int DEFAULT_CAPACITY = 8;

    private final Object[] trays;
    private int front;   // index of the front item
    private int size;    // how many trays are in use
    private StorageMode mode;

    public CircularDequeStorage(StorageMode mode) {
        this(DEFAULT_CAPACITY, mode);
    }

    public CircularDequeStorage(int capacity, StorageMode mode) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        this.trays = new Object[capacity];
        this.front = 0;
        this.size = 0;
        if (mode == null) {
            throw new IllegalArgumentException("Mode is required");
        }
        this.mode = mode;
    }

    // ------------------------------------------------------------------
    // Low level deque operations - all O(1)
    // ------------------------------------------------------------------

    /** Adds to the front side. Front index moves one step "left" (wrapping). */
    private void addFront(T item) throws StorageFullException {
        if (isFull()) {
            throw new StorageFullException(capacity());
        }
        front = (front - 1 + trays.length) % trays.length;
        trays[front] = item;
        size++;
    }

    private T removeFront() throws StorageEmptyException {
        T item = peekFront();
        trays[front] = null;               // let the old object be garbage collected
        front = (front + 1) % trays.length;
        size--;
        return item;
    }

    // ------------------------------------------------------------------
    // FoodStorage interface
    // ------------------------------------------------------------------

    @Override
    public void add(T item) throws StorageFullException {
        if (item == null) {
            throw new IllegalArgumentException("Cannot store an empty (null) item");
        }
        addFront(item);                    // the chef always adds at the front side
    }

    @Override
    public T remove() throws StorageEmptyException {
        if (mode == StorageMode.STACK) {
            return removeFront();          // LIFO - same side we added on
        }
        throw new UnsupportedOperationException("Queue mode not implemented yet");
    }

    @Override
    public T peek() throws StorageEmptyException {
        if (mode == StorageMode.STACK) {
            return peekFront();
        }
        throw new UnsupportedOperationException("Queue mode not implemented yet");
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peekFront() throws StorageEmptyException {
        if (isEmpty()) {
            throw new StorageEmptyException();
        }
        return (T) trays[front];
    }

    @Override
    public List<T> toList() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<T> search(Predicate<T> condition) throws StorageEmptyException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity() {
        return trays.length;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isFull() {
        return size == trays.length;
    }

    @Override
    public StorageMode getMode() {
        return mode;
    }

    @Override
    public void setMode(StorageMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Mode is required");
        }
        this.mode = mode;
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}