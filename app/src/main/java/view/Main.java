package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Main class that sets up the GUI for the drawing recognition app.
 * Includes a drawing panel, result visualization, and control buttons.
 */
public class Main {

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(60, 90, 153);
    private static final Color LIGHT_BG = new Color(250, 250, 250);
    private static final Color DARK_TEXT = new Color(33, 37, 41);
    private static final Color HOVER_BLUE = new Color(100, 149, 237); // light blue

    private static final String[] CATEGORIES;

    static {
        String[] cats = model.Data.Data.CATEGORIES;
        CATEGORIES = new String[cats.length];
        for (int i = 0; i < cats.length; i++) {
            CATEGORIES[i] = cats[i].substring(0, 1).toUpperCase() + cats[i].substring(1);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("AI Drawing Recognition");
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(LIGHT_BG);

        JPanel mainContainer = new JPanel(new BorderLayout(15, 15));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainContainer.setBackground(LIGHT_BG);

        // Left side: drawing panel
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBackground(LIGHT_BG);

        JLabel drawingTitle = new JLabel("Drawing Area");
        drawingTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        drawingTitle.setForeground(PRIMARY_COLOR);

        ZeichenPanel drawingPanel = new ZeichenPanel();
        drawingPanel.setPreferredSize(new Dimension(700, 700));
        drawingPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        leftPanel.add(drawingTitle, BorderLayout.NORTH);
        leftPanel.add(drawingPanel, BorderLayout.CENTER);

        // Right side: result display
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(LIGHT_BG);
        rightPanel.setPreferredSize(new Dimension(350, 700));

        JLabel resultsTitle = new JLabel("Recognition Results");
        resultsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultsTitle.setForeground(PRIMARY_COLOR);

        DiagramPanel diagramPanel = new DiagramPanel();
        diagramPanel.setPreferredSize(new Dimension(350, 250));
        diagramPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        diagramPanel.setBackground(Color.WHITE);

        JLabel bestMatchLabel = new JLabel("Please draw something...");
        bestMatchLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        bestMatchLabel.setForeground(PRIMARY_COLOR);
        bestMatchLabel.setHorizontalAlignment(JLabel.CENTER);
        bestMatchLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Legend panel
        JPanel legendPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        legendPanel.setBackground(LIGHT_BG);

        for (int i = 0; i < CATEGORIES.length; i++) {
            JPanel catPanel = new JPanel(new BorderLayout());
            catPanel.setBackground(LIGHT_BG);

            JPanel colorBox = new JPanel();
            colorBox.setBackground(diagramPanel.getCategoryColor(i));
            colorBox.setPreferredSize(new Dimension(15, 15));

            JLabel catLabel = new JLabel(" " + CATEGORIES[i]);
            catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            catPanel.add(colorBox, BorderLayout.WEST);
            catPanel.add(catLabel, BorderLayout.CENTER);
            legendPanel.add(catPanel);
        }

        JPanel resultsPanel = new JPanel(new BorderLayout(0, 10));
        resultsPanel.setBackground(LIGHT_BG);
        resultsPanel.add(diagramPanel, BorderLayout.CENTER);
        resultsPanel.add(legendPanel, BorderLayout.SOUTH);

        rightPanel.add(resultsTitle, BorderLayout.NORTH);
        rightPanel.add(resultsPanel, BorderLayout.CENTER);
        rightPanel.add(bestMatchLabel, BorderLayout.SOUTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(LIGHT_BG);

        JButton recognizeButton = createStyledButton("Recognize", HOVER_BLUE, Color.WHITE);
        recognizeButton.addActionListener(e -> {
            double[] values = drawingPanel.printArray();
            diagramPanel.setValues(values);
            String bestCategory = controller.GameController.getBestCategory(drawingPanel.getPixelMatrix());
            bestMatchLabel.setText("Recognized: " + bestCategory);
        });

        JButton clearButton = createStyledButton("Clear", HOVER_BLUE, Color.WHITE);
        clearButton.addActionListener(e -> {
            diagramPanel.setValues(new double[]{0, 0, 0, 0, 0});
            drawingPanel.clear();
            bestMatchLabel.setText("Please draw something...");
        });

        buttonPanel.add(recognizeButton);
        buttonPanel.add(clearButton);

        // Final layout
        mainContainer.add(leftPanel, BorderLayout.WEST);
        mainContainer.add(rightPanel, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainContainer);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates a styled button with black default color and hover effect.
     * @param text button label
     * @param hoverColor color to show on hover
     * @param fgColor text color
     * @return configured JButton
     */
    private static JButton createStyledButton(String text, Color hoverColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBackground(Color.BLACK); // default color
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });

        return button;
    }
}
