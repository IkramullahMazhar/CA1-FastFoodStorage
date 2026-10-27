package ie.cct.fastfood.service;

import ie.cct.fastfood.model.FoodType;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Checks every value the user types before it reaches the storage.
 * A Clock is passed in so tests can fix "today" to a known date.
 */
public class FoodValidator {

    public static final int MAX_SHELF_DAYS = 14;

    /** STRICT means impossible dates like 31/02/2026 are rejected. */
    public static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

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

    /** Best-before must be a real date, not in the past, and at most 14 days away. */
    public LocalDate validateBestBefore(String input) throws ValidationException {
        LocalDate date = parseDate(input, "Best-before date");
        LocalDate today = today();
        if (date.isBefore(today)) {
            throw new ValidationException("Best-before date cannot be in the past (today is "
                    + today.format(INPUT_DATE) + ").");
        }
        LocalDate latest = today.plusDays(MAX_SHELF_DAYS);
        if (date.isAfter(latest)) {
            throw new ValidationException("Best-before date can be at most " + MAX_SHELF_DAYS
                    + " days from today (latest allowed: " + latest.format(INPUT_DATE) + ").");
        }
        return date;
    }

    /** Used by the optional search by weight range. */
    public int[] validateWeightRange(String minInput, String maxInput) throws ValidationException {
        int min = parseWholeNumber(minInput, "Minimum weight");
        int max = parseWholeNumber(maxInput, "Maximum weight");
        if (min > max) {
            throw new ValidationException("Minimum weight cannot be bigger than maximum weight.");
        }
        return new int[]{min, max};
    }

    /** Used by the optional search by best-before date range. */
    public LocalDate[] validateDateRange(String fromInput, String toInput) throws ValidationException {
        LocalDate from = parseDate(fromInput, "From date");
        LocalDate to = parseDate(toInput, "To date");
        if (from.isAfter(to)) {
            throw new ValidationException("'From' date must be on or before the 'To' date.");
        }
        return new LocalDate[]{from, to};
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

    private LocalDate parseDate(String input, String fieldName) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty. Use the format dd/MM/yyyy.");
        }
        try {
            return LocalDate.parse(input.trim(), INPUT_DATE);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " '" + input.trim()
                    + "' is not a valid date. Use the format dd/MM/yyyy (e.g. 25/10/2026).");
        }
    }
}