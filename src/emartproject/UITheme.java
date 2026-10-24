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
    public static final Color PRIMARY = new Color(31, 95, 170);
    public static final Color PRIMARY_DARK = new Color(22, 69, 128);
    public static final Color ACCENT = new Color(44, 125, 220);
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(37, 48, 63);
    public static final Color MUTED = new Color(102, 115, 132);
    public static final Color BORDER = new Color(220, 227, 236);
    public static final Color SUCCESS = new Color(25, 135, 84);
    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 14);

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

        UIManager.put("control", BACKGROUND);
        UIManager.put("info", SURFACE);
        UIManager.put("nimbusBase", PRIMARY_DARK);
        UIManager.put("nimbusBlueGrey", new Color(224, 232, 242));
        UIManager.put("nimbusLightBackground", SURFACE);
        UIManager.put("text", TEXT);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("OptionPane.messageFont", new FontUIResource(BASE_FONT));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(BASE_FONT.deriveFont(Font.BOLD)));
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.font", new FontUIResource(BASE_FONT));
        UIManager.put("Button.font", new FontUIResource(BASE_FONT.deriveFont(Font.BOLD)));
        UIManager.put("Button.background", SURFACE);
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
        table.setRowHeight(Math.max(table.getRowHeight(), 34));
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
        heading.setFont(new Font("SansSerif", Font.BOLD, 23));
        header.add(heading, BorderLayout.WEST);

        if (subtitle != null && !subtitle.trim().isEmpty()) {
            JLabel sub = new JLabel(subtitle);
            sub.setForeground(new Color(225, 237, 252));
            sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
            header.add(sub, BorderLayout.EAST);
        }
        return header;
    }

    public static void styleButton(JButton button, boolean primary) {
        button.setFont(BASE_FONT.deriveFont(Font.BOLD, 14f));
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
        frame.revalidate();
        frame.repaint();
    }

    private static void polishComponent(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Color bg = panel.getBackground();
            // Standard light panels become the same soft background; keep colored headers/cards intact.
            if (bg == null || bg.equals(Color.WHITE)
                    || bg.equals(new Color(245, 247, 250))
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
