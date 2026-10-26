package ie.cct.fastfood.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Creates the right subclass for a given food type, so the rest of the
 * program only ever deals with the FoodItem base class.
 */
public final class FoodFactory {

    private FoodFactory() {
        // utility class - no objects needed
    }

    public static FoodItem create(FoodType type, int weightGrams, LocalDate bestBefore, LocalDateTime placedAt) {
        switch (type) {
            case BURGER:
                return new Burger(weightGrams, bestBefore, placedAt);
            case PIZZA:
                return new Pizza(weightGrams, bestBefore, placedAt);
            case FRIES:
                return new Fries(weightGrams, bestBefore, placedAt);
            case SANDWICH:
                return new Sandwich(weightGrams, bestBefore, placedAt);
            case HOTDOG:
                return new Hotdog(weightGrams, bestBefore, placedAt);
            default:
                throw new IllegalArgumentException("Unknown food type: " + type);
        }
    }
}