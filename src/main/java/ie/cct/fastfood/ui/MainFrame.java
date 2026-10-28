package ie.cct.fastfood.ui;

import ie.cct.fastfood.datastructure.StorageMode;
import ie.cct.fastfood.model.FoodItem;
import ie.cct.fastfood.model.FoodType;
import ie.cct.fastfood.service.FoodValidator;
import ie.cct.fastfood.service.StorageManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Main window of the application. It only handles the screen - every
 * operation is passed to the StorageManager, and any exception it throws is
 * shown to the user as a message instead of crashing the program.
 */
public class MainFrame extends JFrame {

    private static final DateTimeFormatter LOG_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String[] COLUMNS = {"Position (from front)", "Name", "Weight (g)",
        "Best before", "Placed in storage", "Status"};

    private final StorageManager manager;

    private final JTextField nameField = new JTextField(14);
    private final JTextField weightField = new JTextField(14);
    private final JTextField dateField = new JTextField(14);
    private final JRadioButton stackButton = new JRadioButton("Stack (LIFO)");
    private final JRadioButton queueButton = new JRadioButton("Queue (FIFO)");
    private final JLabel resultTitle = new JLabel("Results");
    private final JTextArea log = new JTextArea(7, 60);
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final TrayPanel trayPanel;

    public MainFrame(StorageManager manager) {
        super("Fast Food Party Storage - CA1 Algorithms and Constructs");
        this.manager = manager;
        this.trayPanel = new TrayPanel(manager.capacity(), manager.getMode());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildAddPanel(), BorderLayout.WEST);
        root.add(buildCentre(), BorderLayout.CENTER);
        root.add(buildOperationsPanel(), BorderLayout.EAST);
        root.add(buildLogPanel(), BorderLayout.SOUTH);
        setContentPane(root);

        pack();
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);

        log("Application started. Storage capacity: " + manager.capacity() + " trays.");
        refresh();
    }

    // ------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Fast Food Party Storage");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        header.add(title, BorderLayout.WEST);

        JPanel modePanel = new JPanel();
        modePanel.add(new JLabel("How is the chef using the storage?"));
        ButtonGroup group = new ButtonGroup();
        group.add(stackButton);
        group.add(queueButton);
        stackButton.setToolTipText(StorageMode.STACK.getDescription());
        queueButton.setToolTipText(StorageMode.QUEUE.getDescription());
        if (manager.getMode() == StorageMode.STACK) {
            stackButton.setSelected(true);
        } else {
            queueButton.setSelected(true);
        }
        stackButton.addActionListener(e -> changeMode(StorageMode.STACK));
        queueButton.addActionListener(e -> changeMode(StorageMode.QUEUE));
        modePanel.add(stackButton);
        modePanel.add(queueButton);
        header.add(modePanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildAddPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add food item"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(3, 6, 3, 6);

        c.gridy = 0;
        panel.add(new JLabel("Food name:"), c);
        c.gridy = 1;
        panel.add(nameField, c);
        c.gridy = 2;
        panel.add(hint("<html>" + FoodType.allNames() + "</html>"), c);

        c.gridy = 3;
        panel.add(new JLabel("Weight (grams):"), c);
        c.gridy = 4;
        panel.add(weightField, c);
        c.gridy = 5;
        panel.add(hint(weightHint()), c);

        c.gridy = 6;
        panel.add(new JLabel("Best-before date:"), c);
        c.gridy = 7;
        panel.add(dateField, c);
        c.gridy = 8;
        LocalDate latest = manager.today().plusDays(FoodValidator.MAX_SHELF_DAYS);
        panel.add(hint("<html>dd/MM/yyyy - max " + FoodValidator.MAX_SHELF_DAYS
                + " days<br>(latest " + latest.format(FoodValidator.INPUT_DATE) + ")</html>"), c);

        JButton addButton = new JButton("Add to storage");
        addButton.addActionListener(e -> addFood());
        c.gridy = 9;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(12, 6, 3, 6);
        panel.add(addButton, c);

        JButton clearButton = new JButton("Clear fields");
        clearButton.addActionListener(e -> clearFields());
        c.gridy = 10;
        c.insets = new Insets(3, 6, 3, 6);
        panel.add(clearButton, c);

        // pressing Enter in any field also adds the item
        nameField.addActionListener(e -> addFood());
        weightField.addActionListener(e -> addFood());
        dateField.addActionListener(e -> addFood());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildCentre() {
        JPanel centre = new JPanel(new BorderLayout(5, 5));
        trayPanel.setBorder(BorderFactory.createTitledBorder("Storage unit (8 trays)"));
        centre.add(trayPanel, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        JPanel results = new JPanel(new BorderLayout());
        resultTitle.setFont(resultTitle.getFont().deriveFont(Font.BOLD));
        results.add(resultTitle, BorderLayout.NORTH);
        results.add(new JScrollPane(table), BorderLayout.CENTER);
        centre.add(results, BorderLayout.CENTER);
        return centre;
    }

    private JPanel buildOperationsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Operations"));
        panel.add(button("Remove item", this::removeFood));
        panel.add(button("Peek (top item)", this::peekTop));
        panel.add(button("Display all", this::displayAll));
        panel.add(button("Search by name", this::searchByName));
        panel.add(button("Search by weight", this::searchByWeight));
        panel.add(button("Search by date", this::searchByDate));
        JButton exit = button("Exit", this::exitApplication);
        exit.setForeground(new Color(170, 20, 30));
        panel.add(exit);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildLogPanel() {
        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Activity log"));
        panel.add(new JScrollPane(log), BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------

    private void addFood() {
        // added in the next commit
    }

    private void removeFood() {
        // added in the next commit
    }

    private void peekTop() {
        // added in the next commit
    }

    private void displayAll() {
        // added in the next commit
    }

    private void searchByName() {
        // added in the next commit
    }

    private void searchByWeight() {
        // added in the next commit
    }

    private void searchByDate() {
        // added in the next commit
    }

    private void changeMode(StorageMode mode) {
        // added in the next commit
    }

    private void exitApplication() {
        dispose();
        System.exit(0);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void refresh() {
        trayPanel.update(manager.getItemsFrontToBack(), manager.getMode());
    }

    private void clearFields() {
        nameField.setText("");
        weightField.setText("");
        dateField.setText("");
        nameField.requestFocusInWindow();
    }

    private void log(String message) {
        log.append("[" + LocalTime.now().format(LOG_TIME) + "] " + message + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private JLabel hint(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.ITALIC, 11f));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private String weightHint() {
        StringBuilder sb = new StringBuilder("<html>");
        for (FoodType type : FoodType.values()) {
            sb.append(type.getDisplayName()).append(": ").append(type.getMinWeight())
                    .append("-").append(type.getMaxWeight()).append("g<br>");
        }
        return sb.append("</html>").toString();
    }

    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }
}
