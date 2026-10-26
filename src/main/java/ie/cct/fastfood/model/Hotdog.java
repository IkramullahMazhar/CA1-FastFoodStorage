package ie.cct.fastfood.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A hotdog stored on a tray. */
public class Hotdog extends FoodItem {

    public Hotdog(int weightGrams, LocalDate bestBefore, LocalDateTime placedAt) {
        super(weightGrams, bestBefore, placedAt);
    }

    @Override
    public FoodType getType() {
        return FoodType.HOTDOG;
    }

    @Override
    public String getHandlingNote() {
        return "Keep sausage and bun together, reheat before serving";
    }
}