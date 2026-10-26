package ie.cct.fastfood.service;

import ie.cct.fastfood.model.FoodType;
import java.time.Clock;
import java.time.LocalDate;

/**
 * Checks every value the user types before it reaches the storage.
 * A Clock is passed in so tests can fix "today" to a known date.
 */
public class FoodValidator {

    private final Clock clock;

    public FoodValidator(Clock clock) {
        this.clock = clock;
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public FoodType validateName(String input) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Food name cannot be empty. Choose one of: " + FoodType.allNames());
        }
        FoodType type = FoodType.fromName(input);
        if (type == null) {
            throw new ValidationException("'" + input.trim() + "' is not on the menu. Choose one of: "
                    + FoodType.allNames());
        }
        return type;
    }

    /** Weight must be a whole number of grams inside the range for that food. */
    public int validateWeight(String input, FoodType type) throws ValidationException {
        int weight = parseWholeNumber(input, "Weight");
        if (weight < type.getMinWeight() || weight > type.getMaxWeight()) {
            throw new ValidationException(type.getDisplayName() + " weight must be between "
                    + type.getMinWeight() + "g and " + type.getMaxWeight() + "g.");
        }
        return weight;
    }

    // ------------------------------------------------------------------

    private int parseWholeNumber(String input, String fieldName) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
        int value;
        try {
            value = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a whole number in grams (e.g. 250).");
        }
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than 0.");
        }
        return value;
    }

}