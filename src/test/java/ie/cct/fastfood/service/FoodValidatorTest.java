package ie.cct.fastfood.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ie.cct.fastfood.model.FoodType;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/** "Today" is fixed to 20/10/2026 so the date tests always give the same result. */
public class FoodValidatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);
    private final FoodValidator validator = new FoodValidator(
            Clock.fixed(TODAY.atStartOfDay(ZoneId.of("Europe/Dublin")).toInstant(), ZoneId.of("Europe/Dublin")));

    @Test
    public void validNamesAreAcceptedInAnyCase() throws ValidationException {
        assertEquals(FoodType.BURGER, validator.validateName("burger"));
        assertEquals(FoodType.PIZZA, validator.validateName("  PIZZA "));
        assertEquals(FoodType.HOTDOG, validator.validateName("Hot Dog"));
        assertEquals(FoodType.HOTDOG, validator.validateName("hot-dog"));
    }

    @Test
    public void unknownOrEmptyNamesAreRejected() {
        assertThrows(ValidationException.class, () -> validator.validateName("Pasta"));
        assertThrows(ValidationException.class, () -> validator.validateName("Pizzaa"));
        assertThrows(ValidationException.class, () -> validator.validateName(""));
        assertThrows(ValidationException.class, () -> validator.validateName("   "));
        assertThrows(ValidationException.class, () -> validator.validateName(null));
    }

    @Test
    public void weightInsideRangeIsAccepted() throws ValidationException {
        assertEquals(250, validator.validateWeight("250", FoodType.BURGER));
        assertEquals(100, validator.validateWeight("100", FoodType.BURGER));   // lower boundary
        assertEquals(500, validator.validateWeight("500", FoodType.BURGER));   // upper boundary
    }

    @Test
    public void badWeightsAreRejected() {
        assertThrows(ValidationException.class, () -> validator.validateWeight("abc", FoodType.PIZZA));
        assertThrows(ValidationException.class, () -> validator.validateWeight("12.5", FoodType.PIZZA));
        assertThrows(ValidationException.class, () -> validator.validateWeight("-300", FoodType.PIZZA));
        assertThrows(ValidationException.class, () -> validator.validateWeight("0", FoodType.PIZZA));
        assertThrows(ValidationException.class, () -> validator.validateWeight("", FoodType.PIZZA));
        assertThrows(ValidationException.class, () -> validator.validateWeight("99", FoodType.BURGER));
        assertThrows(ValidationException.class, () -> validator.validateWeight("501", FoodType.BURGER));
    }

    @Test
    public void bestBeforeFromTodayUpToFourteenDaysIsAccepted() throws ValidationException {
        assertEquals(TODAY, validator.validateBestBefore("20/10/2026"));
        assertEquals(TODAY.plusDays(14), validator.validateBestBefore("03/11/2026"));
    }

    @Test
    public void badBestBeforeDatesAreRejected() {
        assertThrows(ValidationException.class, () -> validator.validateBestBefore("19/10/2026")); // past
        assertThrows(ValidationException.class, () -> validator.validateBestBefore("04/11/2026")); // 15 days
        assertThrows(ValidationException.class, () -> validator.validateBestBefore("31/02/2026")); // impossible
        assertThrows(ValidationException.class, () -> validator.validateBestBefore("2026-10-25")); // wrong format
        assertThrows(ValidationException.class, () -> validator.validateBestBefore("tomorrow"));
        assertThrows(ValidationException.class, () -> validator.validateBestBefore(""));
    }

    @Test
    public void rangesMustBeInTheRightOrder() throws ValidationException {
        int[] weights = validator.validateWeightRange("100", "300");
        assertEquals(100, weights[0]);
        assertEquals(300, weights[1]);
        assertThrows(ValidationException.class, () -> validator.validateWeightRange("300", "100"));
        assertThrows(ValidationException.class, () -> validator.validateDateRange("30/10/2026", "21/10/2026"));
    }
}