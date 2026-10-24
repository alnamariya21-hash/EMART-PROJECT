package emartproject;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private final LoginFrame loginFrame;
    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        setTitle("E-Mart - Create Account");
        setSize(480, 590);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(loginFrame);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(238, 241, 246));

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(28, 35, 28, 35));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(360, 500));

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        fullNameField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        form.add(title);
        form.add(Box.createVerticalStrut(18));
        addField(form, "Full Name", fullNameField);
        form.add(Box.createVerticalStrut(10));
        addField(form, "Username", usernameField);
        form.add(Box.createVerticalStrut(10));
        addField(form, "Password (at least 6 characters)", passwordField);
        form.add(Box.createVerticalStrut(10));
        addField(form, "Confirm Password", confirmPasswordField);
        form.add(Box.createVerticalStrut(18));

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerButton.addActionListener(e -> register());
        form.add(registerButton);

        form.add(Box.createVerticalStrut(10));
        JButton backButton = new JButton("Back to Login");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        backButton.addActionListener(e -> dispose());
        form.add(backButton);

        background.add(form);
        add(background);
        setVisible(true);
    }

    private void addField(JPanel panel, String label, JComponent input) {
        JLabel text = new JLabel(label);
        text.setFont(new Font("SansSerif", Font.BOLD, 13));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setFont(new Font("SansSerif", Font.PLAIN, 14));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        input.setPreferredSize(new Dimension(290, 36));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(text);
        panel.add(Box.createVerticalStrut(5));
        panel.add(input);
    }

    private void register() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        if (fullName.isEmpty() || username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            java.util.Arrays.fill(password, '\0');
            java.util.Arrays.fill(confirmPassword, '\0');
            return;
        }

        if (!java.util.Arrays.equals(password, confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            java.util.Arrays.fill(password, '\0');
            java.util.Arrays.fill(confirmPassword, '\0');
            return;
        }

        try {
            Database.register(username, fullName, password, "Customer");
            JOptionPane.showMessageDialog(this,
                "Account created successfully! Please log in.");
            loginFrame.registrationCompleted(username);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Registration failed", JOptionPane.ERROR_MESSAGE);
        } finally {
            java.util.Arrays.fill(password, '\0');
            java.util.Arrays.fill(confirmPassword, '\0');
        }
    }
}
