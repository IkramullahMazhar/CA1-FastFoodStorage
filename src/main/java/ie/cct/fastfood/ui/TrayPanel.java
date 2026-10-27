package ie.cct.fastfood.ui;

import ie.cct.fastfood.datastructure.StorageMode;
import ie.cct.fastfood.model.FoodItem;
import ie.cct.fastfood.model.FoodType;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Draws the storage unit as 8 trays in a row so the user can SEE the data
 * structure. The left end is the FRONT side, the right end is the BACK side.
 * The tray that will be removed next is outlined in red.
 */
public class TrayPanel extends JPanel {

    private static final Color EMPTY_FILL = new Color(245, 245, 245);
    private static final Color BORDER = new Color(120, 120, 120);
    private static final Color NEXT_OUT = new Color(200, 30, 45);
    private static final Color TEXT = new Color(30, 30, 30);

    private List<FoodItem> items = new ArrayList<>();
    private int capacity;
    private StorageMode mode;

    public TrayPanel(int capacity, StorageMode mode) {
        this.capacity = capacity;
        this.mode = mode;
        setPreferredSize(new Dimension(820, 200));
        setBackground(Color.WHITE);
    }

    /** Called by the main window after every operation. */
    public void update(List<FoodItem> itemsFrontToBack, StorageMode mode) {
        this.items = new ArrayList<>(itemsFrontToBack);
        this.mode = mode;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // start drawing below the titled border so nothing overlaps its text
        g2.translate(0, getInsets().top);

        int sideWidth = 90;
        int gap = 6;
        int top = 40;
        int slotHeight = 95;
        int usable = getWidth() - 2 * sideWidth - gap * (capacity - 1);
        int slotWidth = Math.max(60, usable / capacity);

        // side labels and arrows
        g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
        g2.setColor(TEXT);
        g2.drawString("FRONT", 18, top - 12);
        g2.drawString("BACK", getWidth() - sideWidth + 22, top - 12);
        drawSideArrows(g2, sideWidth, top, slotHeight);

        int nextOutPosition = items.isEmpty() ? -1 : (mode == StorageMode.STACK ? 0 : items.size() - 1);

        // In queue mode the trays slide through to the back door, so we draw the
        // items packed against the BACK side. In stack mode they sit at the FRONT.
        int offset = mode == StorageMode.QUEUE ? capacity - items.size() : 0;

        for (int i = 0; i < capacity; i++) {
            int x = sideWidth + i * (slotWidth + gap);
            int position = i - offset;
            if (position >= 0 && position < items.size()) {
                drawItem(g2, items.get(position), x, top, slotWidth, slotHeight, position == nextOutPosition);
            } else {
                drawEmptySlot(g2, x, top, slotWidth, slotHeight);
            }
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(11f));
            g2.drawString("Tray " + (i + 1), x + 4, top + slotHeight + 16);
        }

        g2.setColor(TEXT);
        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
        g2.drawString("Mode: " + mode.getDescription() + "    |    " + items.size() + " / " + capacity
                + " trays used", sideWidth, 14);
        g2.dispose();
    }

    private void drawSideArrows(Graphics2D g2, int sideWidth, int top, int slotHeight) {
        int midY = top + slotHeight / 2;
        g2.setStroke(new BasicStroke(2f));
        g2.setFont(getFont().deriveFont(Font.BOLD, 11f));

        // IN is always at the front
        g2.setColor(new Color(20, 120, 60));
        arrow(g2, 10, midY - 15, sideWidth - 12, midY - 15);
        g2.drawString("IN", 14, midY - 21);

        // OUT depends on the mode
        g2.setColor(NEXT_OUT);
        if (mode == StorageMode.STACK) {
            arrow(g2, sideWidth - 12, midY + 18, 10, midY + 18);
            g2.drawString("OUT", 14, midY + 36);
        } else {
            int right = getWidth() - sideWidth;
            arrow(g2, right + 12, midY + 18, getWidth() - 10, midY + 18);
            g2.drawString("OUT", right + 20, midY + 36);
        }
    }

    private void arrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
        int dir = x2 > x1 ? -1 : 1;
        g2.drawLine(x2, y2, x2 + dir * 8, y2 - 5);
        g2.drawLine(x2, y2, x2 + dir * 8, y2 + 5);
    }

    private void drawEmptySlot(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(EMPTY_FILL);
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
        g2.drawRoundRect(x, y, w, h, 12, 12);
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(getFont().deriveFont(Font.ITALIC, 11f));
        centerText(g2, "empty", x, y + h / 2 + 4, w);
    }

    private void drawItem(Graphics2D g2, FoodItem item, int x, int y, int w, int h, boolean nextOut) {
        g2.setColor(colourFor(item.getType()));
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setStroke(new BasicStroke(nextOut ? 3.5f : 1.2f));
        g2.setColor(nextOut ? NEXT_OUT : BORDER);
        g2.drawRoundRect(x, y, w, h, 12, 12);

        g2.setColor(TEXT);
        g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
        centerText(g2, item.getName(), x, y + 30, w);
        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        centerText(g2, item.getWeightGrams() + " g", x, y + 50, w);
        centerText(g2, "BB " + item.getBestBefore().format(FoodItem.DATE_FORMAT).substring(0, 5), x, y + 66, w);
        if (nextOut) {
            g2.setColor(NEXT_OUT);
            g2.setFont(getFont().deriveFont(Font.BOLD, 10f));
            centerText(g2, "NEXT OUT", x, y + 86, w);
        }
    }

    private void centerText(Graphics2D g2, String text, int x, int baseline, int width) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, baseline);
    }

    static Color colourFor(FoodType type) {
        switch (type) {
            case BURGER:
                return new Color(255, 214, 153);
            case PIZZA:
                return new Color(255, 179, 166);
            case FRIES:
                return new Color(255, 240, 150);
            case SANDWICH:
                return new Color(198, 232, 186);
            case HOTDOG:
                return new Color(240, 196, 222);
            default:
                return Color.WHITE;
        }
    }
}