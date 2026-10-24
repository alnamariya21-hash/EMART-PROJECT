package emartproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

/**
 * Shared visual theme for all E-Mart Swing screens.
 * Apply once, before creating any frames.
 */
public final class UITheme {
    public static final Color PRIMARY = new Color(36, 99, 235);
    public static final Color PRIMARY_DARK = new Color(15, 29, 54);
    public static final Color ACCENT = new Color(59, 130, 246);
    public static final Color BACKGROUND = new Color(15, 29, 54);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(37, 48, 63);
    public static final Color MUTED = new Color(102, 115, 132);
    public static final Color BORDER = new Color(218, 226, 239);
    public static final Color SUCCESS = new Color(25, 135, 84);
    private static final Font BASE_FONT = new Font("Times New Roman", Font.PLAIN, 15);

    private UITheme() { }

    public static void apply() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Keep the system's current look and feel if Nimbus is unavailable.
        }

        UIManager.put("control", SURFACE);
        UIManager.put("info", SURFACE);
        UIManager.put("nimbusBase", PRIMARY_DARK);
        UIManager.put("nimbusBlueGrey", new Color(224, 232, 242));
        UIManager.put("nimbusLightBackground", SURFACE);
        UIManager.put("text", TEXT);
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("OptionPane.messageArea.background", SURFACE);
        UIManager.put("OptionPane.buttonAreaBackground", SURFACE);
        UIManager.put("OptionPane.foreground", TEXT);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        UIManager.put("OptionPane.messageFont", new FontUIResource(BASE_FONT));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(BASE_FONT.deriveFont(Font.BOLD)));
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.font", new FontUIResource(BASE_FONT));
        UIManager.put("Button.font", new FontUIResource(BASE_FONT.deriveFont(Font.BOLD)));
        UIManager.put("Button.background", SURFACE);
        UIManager.put("Button.opaque", Boolean.TRUE);
        UIManager.put("Button.foreground", PRIMARY_DARK);
        UIManager.put("Button.margin", new Insets(9, 16, 9, 16));
        UIManager.put("TextField.font", new FontUIResource(BASE_FONT));
        UIManager.put("PasswordField.font", new FontUIResource(BASE_FONT));
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("PasswordField.background", SURFACE);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("ComboBox.font", new FontUIResource(BASE_FONT));
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("Table.font", new FontUIResource(BASE_FONT));
        UIManager.put("Table.background", SURFACE);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.alternateRowColor", new Color(247, 250, 254));
        UIManager.put("Table.selectionBackground", new Color(210, 229, 252));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.background", new Color(231, 239, 249));
        UIManager.put("TableHeader.foreground", PRIMARY_DARK);
        UIManager.put("TableHeader.font", new FontUIResource(BASE_FONT.deriveFont(Font.BOLD)));
        UIManager.put("ScrollPane.background", SURFACE);
        UIManager.put("TitledBorder.titleColor", PRIMARY_DARK);
        UIManager.put("ProgressBar.foreground", ACCENT);

        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(BASE_FONT));
            }
        }
        // Restore important emphasis fonts after the generic font pass.
        UIManager.put("Button.font", new FontUIResource(BASE_FONT.deriveFont(Font.BOLD)));
        UIManager.put("TableHeader.font", new FontUIResource(BASE_FONT.deriveFont(Font.BOLD)));
    }

    public static void styleTable(JTable table) {
        table.setFont(BASE_FONT);
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setSelectionBackground(new Color(210, 229, 252));
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setRowHeight(Math.max(table.getRowHeight(), 36));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(BASE_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(new Color(231, 239, 249));
        table.getTableHeader().setForeground(PRIMARY_DARK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setReorderingAllowed(false);
    }

    public static JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel heading = new JLabel(title);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Times New Roman", Font.BOLD, 25));
        header.add(heading, BorderLayout.WEST);

        if (subtitle != null && !subtitle.trim().isEmpty()) {
            JLabel sub = new JLabel(subtitle);
            sub.setForeground(new Color(225, 237, 252));
            sub.setFont(new Font("Times New Roman", Font.PLAIN, 14));
            header.add(sub, BorderLayout.EAST);
        }
        return header;
    }

    /** Give product category dropdowns a cleaner modern appearance. */
    public static void styleCategoryComboBox(JComboBox<?> combo) {
        combo.setFont(BASE_FONT.deriveFont(Font.PLAIN, 15f));
        combo.setForeground(TEXT);
        combo.setBackground(SURFACE);
        combo.setFocusable(false);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(3, 9, 3, 5)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductCategory) {
                    label.setText(((ProductCategory) value).getLabel());
                }
                label.setFont(BASE_FONT.deriveFont(Font.PLAIN, 15f));
                label.setBorder(new EmptyBorder(7, 9, 7, 9));
                if (isSelected) {
                    label.setBackground(new Color(219, 234, 254));
                    label.setForeground(PRIMARY_DARK);
                } else {
                    label.setBackground(SURFACE);
                    label.setForeground(TEXT);
                }
                return label;
            }
        });
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton("▼");
                arrow.setFont(new Font("SansSerif", Font.BOLD, 10));
                arrow.setForeground(Color.WHITE);
                arrow.setBackground(PRIMARY);
                arrow.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                arrow.setFocusPainted(false);
                arrow.setOpaque(true);
                return arrow;
            }
        });
        combo.setPreferredSize(new Dimension(220, 42));
    }

    public static void styleButton(JButton button, boolean primary) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(BASE_FONT.deriveFont(Font.BOLD, 15f));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary ? PRIMARY : BORDER),
            new EmptyBorder(8, 14, 8, 14)
        ));
        button.setBackground(primary ? PRIMARY : SURFACE);
        button.setForeground(primary ? Color.WHITE : PRIMARY_DARK);
        button.setOpaque(true);
    }

    /** Apply the shared E-Mart look to a completed screen without changing its layout. */
    public static void polishWindow(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND);
        polishComponent(frame.getContentPane());
        styleScrollBars(frame.getContentPane());
        frame.revalidate();
        frame.repaint();
    }

    /** Apply the shared scrollbar design to any page or panel, including nested scroll panes. */
    public static void styleScrollBars(Component component) {
        if (component instanceof JScrollPane) {
            JScrollPane pane = (JScrollPane) component;
            styleScrollBar(pane.getVerticalScrollBar());
            styleScrollBar(pane.getHorizontalScrollBar());
        } else if (component instanceof JScrollBar) {
            styleScrollBar((JScrollBar) component);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                styleScrollBars(child);
            }
        }
    }

    private static void styleScrollBar(JScrollBar bar) {
        bar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(36, 99, 235);
                thumbDarkShadowColor = new Color(29, 78, 216);
                thumbHighlightColor = new Color(96, 165, 250);
                trackColor = new Color(241, 245, 249);
                trackHighlightColor = new Color(226, 232, 240);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setFocusable(false);
                button.setBorderPainted(false);
                button.setVisible(false);
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent component, Rectangle bounds) {
                if (bounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(bounds.x + 2, bounds.y + 2,
                    Math.max(0, bounds.width - 4), Math.max(0, bounds.height - 4), 12, 12);
                g2.dispose();
            }
            @Override
            protected Dimension getMinimumThumbSize() {
                return new Dimension(12, 32);
            }
        });
        bar.setPreferredSize(bar.getOrientation() == JScrollBar.VERTICAL
            ? new Dimension(16, 0) : new Dimension(0, 16));
        bar.setOpaque(true);
        bar.setBackground(new Color(241, 245, 249));
    }

    private static void polishComponent(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Color bg = panel.getBackground();
            // Standard light panels become the same soft background; keep colored headers/cards intact.
            if (bg == null || bg.equals(new Color(245, 247, 250))
                    || bg.equals(new Color(238, 241, 246))
                    || bg.equals(new Color(244, 247, 251))) {
                panel.setBackground(BACKGROUND);
            }
        }
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            Color bg = label.getBackground();
            // Preserve white text on dark headers and other intentionally colored labels.
            if (!Color.WHITE.equals(label.getForeground())
                    && (bg == null || bg.equals(Color.WHITE))) {
                label.setForeground(TEXT);
            }
        }
        if (component instanceof JButton) {
            JButton button = (JButton) component;
            String caption = button.getText() == null ? "" : button.getText().toLowerCase();
            boolean primary = caption.contains("login") || caption.contains("sign in")
                    || caption.contains("register") || caption.contains("pay")
                    || caption.contains("add to cart") || caption.equals("add")
                    || caption.contains("continue") || caption.contains("checkout");
            styleButton(button, primary);
        }
        if (component instanceof JTextField || component instanceof JPasswordField
                || component instanceof JComboBox<?>) {
            JComponent input = (JComponent) component;
            input.setFont(BASE_FONT);
            input.setBackground(SURFACE);
            input.setForeground(TEXT);
            input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 10, 7, 10)
            ));
            Dimension preferred = input.getPreferredSize();
            input.setPreferredSize(new Dimension(Math.max(180, preferred.width), 40));
        }
        if (component instanceof JTable) {
            styleTable((JTable) component);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                polishComponent(child);
            }
        }
    }

}
