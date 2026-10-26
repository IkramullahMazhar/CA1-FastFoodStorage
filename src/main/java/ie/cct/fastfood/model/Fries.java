package ie.cct.fastfood.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A portion of fries stored on a tray. */
public class Fries extends FoodItem {

    public Fries(int weightGrams, LocalDate bestBefore, LocalDateTime placedAt) {
        super(weightGrams, bestBefore, placedAt);
    }

    @Override
    public FoodType getType() {
        return FoodType.FRIES;
    }

    @Override
    public String getHandlingNote() {
        return "Keep dry, re-crisp in fryer before serving";
    }
}