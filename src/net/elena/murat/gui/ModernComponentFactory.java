package net.elena.murat.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * A factory class for creating modern, visually appealing Swing components.
 * This class provides static methods to create styled UI components with
 * consistent color schemes, fonts, and visual effects.
 */
public class ModernComponentFactory {

    // =========================================================================
    // COLOR CONSTANTS
    // =========================================================================
    
    /**
     * Primary blue color used for main actions and highlights
     */
    public static final Color PRIMARY_COLOR = new Color(0.1f, 0.1f, 0.75f, 0.8f);
    
    /**
     * Lighter blue variant for gradients and secondary elements
     */
    public static final Color PRIMARY_LIGHT = new Color(52, 152, 219);
    
    /**
     * Red color for destructive actions and errors
     */
    public static final Color ERROR_COLOR = new Color(231, 76, 60);
    
    /**
     * Color for success states and positive actions
     */
    public static final Color SUCCESS_COLOR = new Color(222, 72, 72);
    
    /**
     * Orange color for warnings and alerts
     */
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    
    /**
     * Dark gray for text and primary content
     */
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    
    /**
     * Medium gray for secondary text and borders
     */
    public static final Color TEXT_SECONDARY = new Color(197, 40, 41);
    
    /**
     * Light gray for backgrounds and disabled states
     */
    public static final Color BACKGROUND_LIGHT = new Color(236, 240, 241);
    
    /**
     * Border color for input fields and separators
     */
    public static final Color BORDER_COLOR = new Color(189, 195, 199);
    
    /**
     * White color for cards and elevated surfaces
     */
    public static final Color SURFACE_COLOR = Color.WHITE;
    
    // =========================================================================
    // FONT CONSTANTS
    // =========================================================================
    
    /**
     * Font for main headings and titles
     */
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 21);
    
    /**
     * Font for body text and regular content
     */
    public static final Font FONT_BODY = new Font("Segoe UI", Font.BOLD, 20);
    
    /**
     * Font for buttons and interactive elements
     */
    public static final Font FONT_BUTTON = new Font("Segoe UI Semibold", Font.BOLD, 24);
    
    /**
     * Monospace font for code, numbers, and technical content
     */
    public static final Font FONT_MONOSPACE = new Font("Consolas", Font.PLAIN, 13);
    
    /**
     * Font for labels and form field descriptions
     */
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 20);
    
    // =========================================================================
    // BORDER CONSTANTS
    // =========================================================================
    
    /**
     * Standard padding for content inside components
     */
    private static final Insets STANDARD_PADDING = new Insets(10, 12, 10, 12);
    
    /**
     * Border for input fields in normal state
     */
    private static final Border INPUT_BORDER = new CompoundBorder(
        new LineBorder(BORDER_COLOR, 1),
        new EmptyBorder(8, 10, 8, 10)
    );
    
    /**
     * Border for input fields in focused state
     */
    private static final Border INPUT_FOCUS_BORDER = new CompoundBorder(
        new LineBorder(PRIMARY_COLOR, 2),
        new EmptyBorder(7, 9, 7, 9)
    );
    
    // =========================================================================
    // TEXT FIELD METHODS
    // =========================================================================
    
    /**
     * Creates a modern styled text field with placeholder support.
     * The field has rounded corners, focus highlighting, and clean typography.
     *
     * @param initialText the placeholder text to show when field is empty
     * @param columns the number of columns for the text field width
     * @return a styled JTextField component
     */
	public static JTextField createTextField(String initialText, int columns) {
		JTextField field = new JTextField(initialText, columns);
		field.setFont(FONT_BODY);
		field.setForeground(TEXT_PRIMARY);
		field.setBackground(SURFACE_COLOR);
		field.setBorder(INPUT_BORDER);
		field.setCaretColor(PRIMARY_COLOR);
		return field;
	}
    
    /**
     * Creates a modern styled text field with default width.
     *
     * @param placeholder the placeholder text to show when field is empty
     * @return a styled JTextField component with 20 columns
     */
    public static JTextField createTextField(String placeholder) {
        return createTextField(placeholder, 20);
    }
    
    // =========================================================================
    // BUTTON METHODS
    // =========================================================================
    /**
     * Creates a modern styled button with hover effect.
     * Button foreground turns green on mouse hover.
     *
     * @param text the button text
     * @param backgroundColor the primary background color for the button
     * @return a styled JButton component with hover effect
     */
    public static JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
            
                // Paint rounded rectangle background
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
                g2.dispose();
                super.paintComponent(g);
            }
        
            @Override
            public void setContentAreaFilled(boolean b) {
                super.setContentAreaFilled(false);
            }
        };
    
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new CompoundBorder(
            new EmptyBorder(10, 20, 10, 20),
            new LineBorder(Color.ORANGE, 0)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
    
        // Add mouse listeners for hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(SUCCESS_COLOR); // Color on hover
            }
        
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE); // Color when not hovering
            }
            
			@Override
			public void mousePressed(MouseEvent e) {
				button.setForeground(Color.MAGENTA);
			}
    
			@Override
			public void mouseReleased(MouseEvent e) {
				if (button.contains(e.getPoint())) {
					button.setForeground(SUCCESS_COLOR); // Hover still
					} else {
						button.setForeground(Color.WHITE); // Out of button
					}
			}
		});
    
        return button;
    }
    
    /**
     * Creates a primary action button with the default primary color.
     *
     * @param text the button text
     * @return a primary styled JButton component
     */
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_COLOR);
    }
    
    /**
     * Creates a success button for positive actions like save or confirm.
     *
     * @param text the button text
     * @return a success styled JButton component
     */
    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS_COLOR);
    }
    
    /**
     * Creates a warning button for cautionary actions.
     *
     * @param text the button text
     * @return a warning styled JButton component
     */
    public static JButton createWarningButton(String text) {
        return createButton(text, WARNING_COLOR);
    }
    
    /**
     * Creates an error button for destructive actions like delete or cancel.
     *
     * @param text the button text
     * @return an error styled JButton component
     */
    public static JButton createErrorButton(String text) {
        return createButton(text, ERROR_COLOR);
    }
    
    // =================
    //
    // =================
    /**
	 * Creates a modern styled tabbed pane with consistent styling.
     *
     * @return a styled JTabbedPane component
     */
    public static JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setForeground(PRIMARY_COLOR);
        tabbedPane.setFont(FONT_HEADING);
        tabbedPane.setBackground(BACKGROUND_LIGHT);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
        UIManager.put("TabbedPane.selected", PRIMARY_LIGHT);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND_LIGHT);
    
        return tabbedPane;
    }

    // =========================================================================
    // LABEL METHODS
    // =========================================================================
    
    /**
     * Enumeration of label types for consistent styling.
     */
    public enum LabelType {
        HEADING,
        BODY,
        LABEL,
        MONOSPACE
    }
    
    /**
     * Creates a modern styled label with consistent typography.
     *
     * @param text the label text
     * @param type the label type (heading, body, or label)
     * @return a styled JLabel component
     */
    public static JLabel createLabel(String text, LabelType type) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        
        switch (type) {
            case HEADING:
                label.setFont(FONT_HEADING);
                label.setForeground(TEXT_PRIMARY);
                label.setBorder(new CompoundBorder(
                    new EmptyBorder(0, 0, 5, 0),
                    BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR)
                ));
                break;
                
            case BODY:
                label.setFont(FONT_BODY);
                label.setForeground(TEXT_PRIMARY);
                break;
                
            case LABEL:
                label.setFont(FONT_LABEL);
                label.setForeground(TEXT_SECONDARY);
                break;
                
            case MONOSPACE:
                label.setFont(FONT_MONOSPACE);
                label.setForeground(TEXT_PRIMARY);
                break;
        }
        
        return label;
    }
    
    /**
     * Creates a heading label with larger, bold text.
     *
     * @param text the heading text
     * @return a heading styled JLabel component
     */
    public static JLabel createHeadingLabel(String text) {
        return createLabel(text, LabelType.HEADING);
    }
    
    /**
     * Creates a body label for regular text content.
     *
     * @param text the body text
     * @return a body styled JLabel component
     */
    public static JLabel createBodyLabel(String text) {
        return createLabel(text, LabelType.BODY);
    }
    
    /**
     * Creates a form label for field descriptions.
     *
     * @param text the label text
     * @return a form label styled JLabel component
     */
    public static JLabel createFormLabel(String text) {
        return createLabel(text, LabelType.LABEL);
    }
    
    /**
     * Creates a monospace label for code or technical content.
     *
     * @param text the label text
     * @return a monospace styled JLabel component
     */
    public static JLabel createMonospaceLabel(String text) {
        return createLabel(text, LabelType.MONOSPACE);
    }
    
    // =========================================================================
    // PANEL METHODS
    // =========================================================================
    
    /**
     * Creates a card panel with subtle shadow and rounded corners.
     * Suitable for grouping related content with visual separation.
     *
     * @return a styled JPanel with card appearance
     */
    public static JPanel createCardPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw subtle shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                
                // Draw white card surface
                g2.setColor(SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Draw border
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2.dispose();
            }
        };
    }
    
    /**
     * Creates a form panel with consistent spacing for form elements.
     *
     * @return a styled JPanel optimized for form layout
     */
    public static JPanel createFormPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    // =========================================================================
    // TEXT AREA METHODS
    // =========================================================================
    
    /**
     * Creates a modern styled text area with scroll support.
     *
     * @param rows the number of rows to display
     * @param columns the number of columns to display
     * @return a styled JTextArea inside a JScrollPane
     */
    public static JScrollPane createScrolledTextArea(int rows, int columns) {
        JTextArea textArea = createTextArea(rows, columns);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        return scrollPane;
    }
    
    /**
     * Creates a modern styled text area.
     *
     * @param rows the number of rows to display
     * @param columns the number of columns to display
     * @return a styled JTextArea component
     */
    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(FONT_BODY);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBackground(SURFACE_COLOR);
        textArea.setBorder(INPUT_BORDER);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        return textArea;
    }
    
    // =========================================================================
    // CHECKBOX METHODS
    // =========================================================================
    
    /**
     * Custom checkbox icon with modern styling.
     */
    private static class CheckboxIcon implements Icon {
        private static final int SIZE = 18;
        private final boolean selected;
        
        public CheckboxIcon(boolean selected) {
            this.selected = selected;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw checkbox border
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(x, y, SIZE, SIZE, 4, 4);
            
            // Draw checkbox fill if selected
            if (selected) {
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(x + 2, y + 2, SIZE - 3, SIZE - 3, 2, 2);
                
                // Draw checkmark
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x + 5, y + 9, x + 8, y + 12);
                g2.drawLine(x + 8, y + 12, x + 13, y + 5);
            }
            
            g2.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return SIZE;
        }
        
        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
    
    /**
     * Creates a modern styled checkbox with custom colors.
     *
     * @param text the checkbox label text
     * @return a styled JCheckBox component
     */
    public static JCheckBox createCheckbox(String text) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(FONT_BODY);
        checkbox.setForeground(TEXT_PRIMARY);
        checkbox.setBackground(Color.WHITE);
        checkbox.setFocusPainted(false);
        checkbox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Customize checkbox icon colors
        checkbox.setIcon(new CheckboxIcon(false));
        checkbox.setSelectedIcon(new CheckboxIcon(true));
        checkbox.setPressedIcon(new CheckboxIcon(true));
        
        return checkbox;
    }
    
    // =========================================================================
    // COMBO BOX METHODS
    // =========================================================================
    
    /**
     * Creates a modern styled combo box.
     *
     * @param items the items to display in the combo box
     * @return a styled JComboBox component
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(FONT_BODY);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBackground(SURFACE_COLOR);
        comboBox.setBorder(INPUT_BORDER);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setFont(FONT_BODY);
                    label.setBorder(new EmptyBorder(5, 10, 5, 10));
                }
                return c;
            }
        });
        
        return comboBox;
    }
    
    // =========================================================================
    // UTILITY METHODS
    // =========================================================================
    
    /**
     * Applies a consistent margin to a component.
     *
     * @param component the component to add margin to
     * @param top top margin in pixels
     * @param left left margin in pixels
     * @param bottom bottom margin in pixels
     * @param right right margin in pixels
     */
    public static void setMargin(JComponent component, int top, int left, 
                                int bottom, int right) {
        component.setBorder(new EmptyBorder(top, left, bottom, right));
    }
    
    /**
     * Creates a separator with consistent styling.
     *
     * @return a styled JSeparator component
     */
    public static JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        separator.setBackground(new Color(230, 230, 230));
        return separator;
    }
    
    /**
     * Creates a horizontal separator with consistent styling.
     *
     * @return a styled JSeparator component
     */
    public static JSeparator createHorizontalSeparator() {
        JSeparator separator = createSeparator();
        separator.setOrientation(SwingConstants.HORIZONTAL);
        return separator;
    }
    
    /**
     * Creates a vertical separator with consistent styling.
     *
     * @return a styled JSeparator component
     */
    public static JSeparator createVerticalSeparator() {
        JSeparator separator = createSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        return separator;
    }
    
    /**
     * Sets up a GridBagConstraints object with consistent defaults.
     *
     * @return a configured GridBagConstraints object
     */
    public static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }
    
    /**
     * Sets up a GridBagConstraints object with vertical fill.
     *
     * @return a configured GridBagConstraints object
     */
    public static GridBagConstraints createGridBagConstraintsVertical() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }
    
}
