package ie.cct.fastfood.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A pizza stored on a tray. */
public class Pizza extends FoodItem {

    public Pizza(int weightGrams, LocalDate bestBefore, LocalDateTime placedAt) {
        super(weightGrams, bestBefore, placedAt);
    }

    @Override
    public FoodType getType() {
        return FoodType.PIZZA;
    }

    @Override
    public String getHandlingNote() {
        return "Store flat in its box, do not stack on top";
    }
}