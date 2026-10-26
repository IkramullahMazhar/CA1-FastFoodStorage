package ie.cct.fastfood.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A sandwich stored on a tray. */
public class Sandwich extends FoodItem {

    public Sandwich(int weightGrams, LocalDate bestBefore, LocalDateTime placedAt) {
        super(weightGrams, bestBefore, placedAt);
    }

    @Override
    public FoodType getType() {
        return FoodType.SANDWICH;
    }

    @Override
    public String getHandlingNote() {
        return "Serve cold, keep covered";
    }
}