package emartproject;

import javax.swing.*;
import java.awt.*;

public class BillFrame extends JFrame {

    public BillFrame(String customerName, String items, double total) {
        setTitle("E-Mart - Bill");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("E-MART BILL", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JTextArea bill = new JTextArea();
        bill.setEditable(false);
        bill.setFont(new Font("Monospaced", Font.PLAIN, 14));
        bill.setText(
                "             E-MART STORE\n"
                + "----------------------------------\n"
                + "Customer: " + customerName + "\n"
                + "Items: " + items + "\n"
                + "----------------------------------\n"
                + String.format("TOTAL: $%.2f\n", total)
                + "Payment: Not processed (demo checkout)\n"
                + "Thank you for shopping!"
        );

        bill.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(new JScrollPane(bill), BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Cart");
        JButton closeButton = new JButton("Close");

        backButton.addActionListener(e -> {
            dispose();
            new CartFrame(customerName);
        });

        closeButton.addActionListener(e -> {
            dispose();
            new CustomerDashboard(customerName);
        });

        JPanel buttons = new JPanel();
        buttons.add(backButton);
        buttons.add(closeButton);

        add(buttons, BorderLayout.SOUTH);
        setVisible(true);
    }
}
