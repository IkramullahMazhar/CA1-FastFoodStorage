package ie.cct.fastfood.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Base class for every food item placed on a tray in the storage unit.
 * It holds the details the brief asks us to track: name, weight,
 * best-before date and the time the item was placed into storage.
 *
 * The class is abstract because there is no such thing as a plain
 * "food item" in the restaurant - it is always a Burger, Pizza, etc.
 */
public abstract class FoodItem {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final int weightGrams;
    private final LocalDate bestBefore;
    private final LocalDateTime placedAt;

    protected FoodItem(int weightGrams, LocalDate bestBefore, LocalDateTime placedAt) {
        if (weightGrams <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0 grams");
        }
        this.weightGrams = weightGrams;
        this.bestBefore = Objects.requireNonNull(bestBefore, "Best-before date is required");
        this.placedAt = Objects.requireNonNull(placedAt, "Placed-at time is required");
    }

    /** Each subclass says which of the five food types it is. */
    public abstract FoodType getType();

    /** A short handling note - each food type gives its own (polymorphism). */
    public abstract String getHandlingNote();

    public String getName() {
        return getType().getDisplayName();
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public boolean isExpired(LocalDate today) {
        return today.isAfter(bestBefore);
    }

    public long daysUntilExpiry(LocalDate today) {
        return ChronoUnit.DAYS.between(today, bestBefore);
    }

    /** One line summary used in the log and in the tray view. */
    public String toShortString() {
        return getName() + " (" + weightGrams + "g)";
    }

    /** Full details, used when a search result is displayed. */
    public String toDetailString() {
        return "Name: " + getName()
                + " | Weight: " + weightGrams + "g"
                + " | Best before: " + bestBefore.format(DATE_FORMAT)
                + " | Placed in storage: " + placedAt.format(TIME_FORMAT)
                + " | Note: " + getHandlingNote();
    }

    @Override
    public String toString() {
        return toDetailString();
    }
}