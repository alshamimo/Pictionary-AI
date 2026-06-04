package view;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * A panel that displays animated bar graphs representing category probabilities.
 * Used in the GUI to visualize classification results.
 */
public class DiagramPanel extends JPanel {

    private double[] targetValues;
    private double[] animatedValues;
    private final String[] categories = {"Apple", "Candle", "Eyeglasses", "Fork", "Star"};
    private final Color[] colors = {
            new Color(255, 99, 132),   // Apple - Red
            new Color(255, 159, 64),   // Candle - Orange
            new Color(75, 192, 192),   // Eyeglasses - Teal
            new Color(153, 102, 255),  // Fork - Purple
            new Color(255, 205, 86)    // Star - Yellow
    };

    private Timer timer;

    public DiagramPanel() {
        this.targetValues = new double[] {0, 0, 0, 0, 0};
        this.animatedValues = new double[] {0, 0, 0, 0, 0};
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    /**
     * Sets new values to be displayed and starts animation.
     * @param newValues array of five probability values (0.0 - 1.0)
     */
    public void setValues(double[] newValues) {
        if (newValues.length != 5) throw new IllegalArgumentException("Exactly 5 values expected!");
        System.arraycopy(newValues, 0, targetValues, 0, 5);

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(16, e -> animateStep()); // ~60fps animation
        timer.start();
    }

    /**
     * Animates the transition between old and new bar heights.
     */
    private void animateStep() {
        boolean done = true;

        for (int i = 0; i < targetValues.length; i++) {
            double diff = targetValues[i] - animatedValues[i];
            if (Math.abs(diff) > 0.005) {
                animatedValues[i] += diff * 0.15;
                done = false;
            } else {
                animatedValues[i] = targetValues[i];
            }
        }

        repaint();

        if (done) {
            timer.stop();
        }
    }

    /**
     * Returns the display color for a given category index.
     * @param index category index
     * @return corresponding Color
     */
    public Color getCategoryColor(int index) {
        if (index >= 0 && index < colors.length) {
            return colors[index];
        }
        return Color.GRAY;
    }

    /**
     * Draws the bar graph and all category labels.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int barWidth = Math.min(width / 8, 60);
        int maxBarHeight = height - 100;
        int startX = (width - (barWidth * 5 + 40)) / 2;

        // Draw horizontal guide lines and percentage markers
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2d.setColor(new Color(200, 200, 200));
        for (int i = 0; i <= 10; i++) {
            int y = height - 50 - i * maxBarHeight / 10;
            g2d.drawLine(startX - 10, y, width - 30, y);
            g2d.setColor(new Color(120, 120, 120));
            g2d.drawString(i * 10 + "%", 5, y + 3);
            g2d.setColor(new Color(200, 200, 200));
        }

        // Draw animated bars and category labels
        for (int i = 0; i < animatedValues.length; i++) {
            double value = Math.max(0.0, Math.min(animatedValues[i], 1.0));
            int barHeight = (int) (value * maxBarHeight);

            int x = startX + i * (barWidth + 10);
            int y = height - 50 - barHeight;

            g2d.setColor(colors[i]);
            g2d.fillRoundRect(x, y, barWidth, barHeight, 10, 10);

            // Draw outline for visual clarity
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRoundRect(x, y, barWidth, barHeight, 10, 10);

            // Draw percentage value above the bar
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            String percent = (int)(value * 100) + "%";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(percent);
            g2d.drawString(percent, x + (barWidth - textWidth) / 2, y - 6);

            // Draw category label below the bar
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(categories[i]);
            g2d.drawString(categories[i], x + (barWidth - textWidth) / 2, height - 30);
        }
    }
}
