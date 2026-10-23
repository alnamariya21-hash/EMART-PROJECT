package emartproject;

import javax.swing.*;
import java.sql.SQLException;

/** E-Mart launcher: one MySQL schema, one persistent data store. */
public class EMartProject {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String user = System.getenv("EMART_DB_USER");
            if (user == null || user.trim().isEmpty()) {
                JTextField usernameField = new JTextField("root");
                JPasswordField passwordField = new JPasswordField();
                JPanel panel = new JPanel(new java.awt.GridLayout(0, 1, 4, 4));
                panel.add(new JLabel("Local MySQL username:"));
                panel.add(usernameField);
                panel.add(new JLabel("Local MySQL password:"));
                panel.add(passwordField);
                if (JOptionPane.showConfirmDialog(null, panel, "E-Mart MySQL setup",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                    return;
                }
                Database.configure(usernameField.getText().trim(), passwordField.getPassword());
            }
            try {
                Database.initialize();
                DataStore.loadAll();
                new LoginFrame();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                    "Unable to initialize E-Mart MySQL database.\n"
                    + ex.getMessage() + "\n\nInstall/start MySQL 8+, then launch with run.bat.",
                    "Database connection failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
