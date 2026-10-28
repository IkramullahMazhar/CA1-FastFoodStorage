package ie.cct.fastfood;

import ie.cct.fastfood.datastructure.CircularDequeStorage;
import ie.cct.fastfood.datastructure.FoodStorage;
import ie.cct.fastfood.datastructure.StorageMode;
import ie.cct.fastfood.model.FoodItem;
import ie.cct.fastfood.service.FoodValidator;
import ie.cct.fastfood.service.StorageManager;
import ie.cct.fastfood.ui.MainFrame;
import java.time.Clock;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Starts the application. */
public class Main {

    public static void main(String[] args) {
        Clock clock = Clock.systemDefaultZone();
        FoodStorage<FoodItem> storage = new CircularDequeStorage<>(StorageMode.STACK);
        StorageManager manager = new StorageManager(storage, new FoodValidator(clock), clock);

        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // not a problem - Java falls back to the default look and feel
            }
            new MainFrame(manager).setVisible(true);
        });
    }
}
