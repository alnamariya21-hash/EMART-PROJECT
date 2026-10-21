package emartproject;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginFrame() {
        setTitle("E-Mart Login");
        setSize(600, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(238, 241, 246));

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(28, 35, 30, 35));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(430, 470));

        JLabel small = new JLabel("Please enter your details");
        small.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel title = new JLabel("WELCOME TO E-Mart");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleBox = new JComboBox<>(new String[]{"Customer", "Admin"});

        form.add(small);
        form.add(Box.createVerticalStrut(12));
        form.add(title);
        form.add(Box.createVerticalStrut(28));

        addField(form, "Username", usernameField);
        form.add(Box.createVerticalStrut(15));

        addField(form, "Password", passwordField);
        form.add(Box.createVerticalStrut(15));

        addField(form, "Login As", roleBox);
        form.add(Box.createVerticalStrut(25));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45));

        loginButton.addActionListener(e -> login());
        form.add(loginButton);

        background.add(form);
        add(background);
        setVisible(true);
    }

    private void addField(JPanel panel, String label,
                          JComponent input) {
        JLabel text = new JLabel(label);
        text.setFont(new Font("SansSerif", Font.BOLD, 14));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        input.setFont(new Font("SansSerif", Font.PLAIN, 15));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        input.setPreferredSize(new Dimension(340, 42));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(text);
        panel.add(Box.createVerticalStrut(7));
        panel.add(input);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password =
                new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter username and password.");
            return;
        }

        if ("Admin".equals(role)) {
            if (username.equals("admin")
                    && password.equals("admin123")) {

                JOptionPane.showMessageDialog(
                        this, "Admin Login Successful!");

                dispose();
                new AdminDashboard();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid admin login.\n"
                        + "Username: admin\n"
                        + "Password: admin123",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(
                    this, "Welcome to E-Mart, " + username + "!");

            dispose();
            new CustomerDashboard(username);
        }
    }
}
