package ie.cct.fastfood.model;

import java.util.Locale;

/**
 * The five food types the restaurant stores for parties.
 * Each type has its own sensible weight range (grams) which is used
 * when validating user input.
 */
public enum FoodType {

    BURGER("Burger", 100, 500),
    PIZZA("Pizza", 200, 1500),
    FRIES("Fries", 50, 400),
    SANDWICH("Sandwich", 100, 600),
    HOTDOG("Hotdog", 80, 400);

    private final String displayName;
    private final int minWeight;
    private final int maxWeight;

    FoodType(String displayName, int minWeight, int maxWeight) {
        this.displayName = displayName;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinWeight() {
        return minWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    /**
     * Finds a food type from what the user typed. Not case sensitive and
     * ignores spaces/hyphens, so "hot dog", "Hot-Dog" and "HOTDOG" all match.
     *
     * @return the matching type, or null if the name is not one of the five
     */
    public static FoodType fromName(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = input.trim().replace(" ", "").replace("-", "").toUpperCase(Locale.ROOT);
        for (FoodType type : values()) {
            if (type.name().equals(cleaned)) {
                return type;
            }
        }
        return null;
    }

    /** Comma separated list of names, used in prompts and error messages. */
    public static String allNames() {
        StringBuilder sb = new StringBuilder();
        for (FoodType type : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(type.displayName);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return displayName;
    }
}