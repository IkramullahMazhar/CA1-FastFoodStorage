package ie.cct.fastfood.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A burger stored on a tray. */
public class Burger extends FoodItem {

    public Burger(int weightGrams, LocalDate bestBefore, LocalDateTime placedAt) {
        super(weightGrams, bestBefore, placedAt);
    }

    @Override
    public FoodType getType() {
        return FoodType.BURGER;
    }

    @Override
    public String getHandlingNote() {
        return "Keep wrapped, reheat before serving";
    }
}