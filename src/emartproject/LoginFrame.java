package emartproject;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginFrame() {
        setTitle("E-Mart Login");
        setSize(480, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(238, 241, 246));

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(360, 400));

        JLabel title = new JLabel("WELCOME TO E-MART");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleBox = new JComboBox<>(new String[]{"Customer", "Admin"});

        form.add(title);
        form.add(Box.createVerticalStrut(28));
        addField(form, "Username", usernameField);
        form.add(Box.createVerticalStrut(15));
        addField(form, "Password", passwordField);
        form.add(Box.createVerticalStrut(15));
        addField(form, "Login As", roleBox);
        form.add(Box.createVerticalStrut(24));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.addActionListener(e -> login());
        form.add(loginButton);

        form.add(Box.createVerticalStrut(12));
        JButton registerButton = new JButton("Create New Account");
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerButton.addActionListener(e -> new RegisterFrame(this));
        form.add(registerButton);

        background.add(form);
        add(background);
        setVisible(true);
    }

    private void addField(JPanel panel, String label, JComponent input) {
        JLabel text = new JLabel(label);
        text.setFont(new Font("SansSerif", Font.BOLD, 14));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setFont(new Font("SansSerif", Font.PLAIN, 15));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        input.setPreferredSize(new Dimension(290, 40));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(text);
        panel.add(Box.createVerticalStrut(7));
        panel.add(input);
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
