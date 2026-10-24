package emartproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    private static final Color NAVY = new Color(20, 37, 68);
    private static final Color BLUE = new Color(39, 100, 210);
    private static final Color PALE = new Color(36, 99, 235);
    private static final Color MUTED = new Color(105, 119, 140);

    public LoginFrame() {
        setTitle("E-Mart Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(620, 680));
        setSize(700, 740);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(PALE);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 232, 243)),
            new EmptyBorder(28, 34, 28, 34)
        ));
        card.setPreferredSize(new Dimension(470, 630));
        card.setMinimumSize(new Dimension(470, 630));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel mark = new JLabel("E");
        mark.setOpaque(true);
        mark.setBackground(BLUE);
        mark.setForeground(Color.WHITE);
        mark.setFont(new Font("Segoe UI", Font.BOLD, 25));
        mark.setHorizontalAlignment(SwingConstants.CENTER);
        mark.setPreferredSize(new Dimension(52, 48));
        JLabel brandName = new JLabel("  E-MART");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 23));
        brandName.setForeground(NAVY);
        brand.add(mark);
        brand.add(brandName);

        JLabel title = new JLabel("Welcome back", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 29));
        title.setForeground(NAVY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue shopping", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleBox = new JComboBox<>(new String[]{"Customer", "Admin"});
        styleInput(usernameField);
        styleInput(passwordField);
        styleInput(roleBox);

        card.add(brand);
        card.add(Box.createVerticalStrut(28));
        card.add(title);
        card.add(Box.createVerticalStrut(7));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        addField(card, "Username", usernameField);
        card.add(Box.createVerticalStrut(18));
        addField(card, "Password", passwordField);
        card.add(Box.createVerticalStrut(18));
        addField(card, "Sign in as", roleBox);
        card.add(Box.createVerticalStrut(26));

        JButton loginButton = new JButton("Sign In");
        styleButton(loginButton, BLUE, Color.WHITE);
        loginButton.addActionListener(e -> login());
        card.add(loginButton);

        card.add(Box.createVerticalStrut(12));
        JButton registerButton = new JButton("Create a Customer Account");
        styleButton(registerButton, Color.WHITE, BLUE);
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(210, 223, 245)));
        registerButton.addActionListener(e -> new RegisterFrame(this));
        card.add(registerButton);

        card.add(Box.createVerticalStrut(20));
        JLabel footer = new JLabel("Simple shopping. A better experience.");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footer.setForeground(MUTED);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(footer);

        root.add(card);
        setContentPane(root);
        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }

    private void addField(JPanel panel, String label, JComponent input) {
        JLabel text = new JLabel(label);
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setForeground(NAVY);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        input.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(text);
        panel.add(Box.createVerticalStrut(8));
        panel.add(input);
    }

    private void styleInput(JComponent input) {
        input.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        input.setForeground(NAVY);
        input.setBackground(Color.WHITE);
        input.setPreferredSize(new Dimension(370, 45));
        input.setMinimumSize(new Dimension(370, 45));
        input.setMaximumSize(new Dimension(370, 45));
        input.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 219, 232)),
            new EmptyBorder(8, 11, 8, 11)
        ));
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(background);
        button.setOpaque(true);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMinimumSize(new Dimension(370, 48));
        button.setMaximumSize(new Dimension(370, 48));
        button.setPreferredSize(new Dimension(370, 48));
        button.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        String role = (String) roleBox.getSelectedItem();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Enter username and password.");
            java.util.Arrays.fill(password, '\0');
            return;
        }

        try {
            if (!Database.authenticate(username, password, role)) {
                JOptionPane.showMessageDialog(this,
                    "Invalid credentials. Please check your details or create a customer account.");
                return;
            }
            if ("Admin".equals(role)) {
                DataStore.clearSession();
                dispose();
                new AdminDashboard();
            } else {
                DataStore.selectCustomer(username);
                dispose();
                new CustomerDashboard(Database.getFullName(username));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Database error", JOptionPane.ERROR_MESSAGE);
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }

    void registrationCompleted(String username) {
        usernameField.setText(username);
        passwordField.setText("");
        roleBox.setSelectedItem("Customer");
        setVisible(true);
        toFront();
        passwordField.requestFocusInWindow();
    }
}
