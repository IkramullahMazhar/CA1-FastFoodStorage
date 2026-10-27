package ie.cct.fastfood.service;

import ie.cct.fastfood.datastructure.FoodStorage;
import ie.cct.fastfood.datastructure.StorageEmptyException;
import ie.cct.fastfood.datastructure.StorageFullException;
import ie.cct.fastfood.datastructure.StorageMode;
import ie.cct.fastfood.model.FoodFactory;
import ie.cct.fastfood.model.FoodItem;
import ie.cct.fastfood.model.FoodType;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Sits between the GUI and the data structure. The GUI passes in raw text,
 * the manager validates it, builds the FoodItem and talks to the storage.
 * Keeping this out of the GUI means it can be unit tested on its own.
 */
public class StorageManager {

    private final FoodStorage<FoodItem> storage;
    private final FoodValidator validator;
    private final Clock clock;

    public StorageManager(FoodStorage<FoodItem> storage, FoodValidator validator, Clock clock) {
        this.storage = storage;
        this.validator = validator;
        this.clock = clock;
    }

    /** Validates the three inputs, records the current time and stores the item. */
    public FoodItem addFood(String name, String weight, String bestBefore)
            throws ValidationException, StorageFullException {
        if (storage.isFull()) {
            // check first so the user is not asked to fix inputs for nothing
            throw new StorageFullException(storage.capacity());
        }
        FoodType type = validator.validateName(name);
        int grams = validator.validateWeight(weight, type);
        LocalDate date = validator.validateBestBefore(bestBefore);

        FoodItem item = FoodFactory.create(type, grams, date, LocalDateTime.now(clock));
        storage.add(item);
        return item;
    }

    public FoodItem removeFood() throws StorageEmptyException {
        return storage.remove();
    }

    /** The item at the "top" - the next one that will come out. */
    public FoodItem peekNext() throws StorageEmptyException {
        return storage.peek();
    }

    /** The item most recently placed at the front side. */
    public FoodItem peekLastAdded() throws StorageEmptyException {
        return storage.peekFront();
    }

    /** All items front to back. Throws if there is nothing to display. */
    public List<FoodItem> displayAll() throws StorageEmptyException {
        if (storage.isEmpty()) {
            throw new StorageEmptyException();
        }
        return storage.toList();
    }

    public List<FoodItem> searchByName(String name) throws ValidationException, StorageEmptyException {
        FoodType type = validator.validateName(name);
        return storage.search(item -> item.getType() == type);
    }

    public List<FoodItem> searchByWeight(String min, String max) throws ValidationException, StorageEmptyException {
        int[] range = validator.validateWeightRange(min, max);
        return storage.search(item -> item.getWeightGrams() >= range[0] && item.getWeightGrams() <= range[1]);
    }

    public List<FoodItem> searchByBestBefore(String from, String to) throws ValidationException, StorageEmptyException {
        LocalDate[] range = validator.validateDateRange(from, to);
        return storage.search(item -> !item.getBestBefore().isBefore(range[0])
                && !item.getBestBefore().isAfter(range[1]));
    }

    public StorageMode getMode() {
        return storage.getMode();
    }

    public void setMode(StorageMode mode) {
        storage.setMode(mode);
    }

    public List<FoodItem> getItemsFrontToBack() {
        return storage.toList();
    }

    public int size() {
        return storage.size();
    }

    public int capacity() {
        return storage.capacity();
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public boolean isFull() {
        return storage.isFull();
    }

    public LocalDate today() {
        return validator.today();
    }
}