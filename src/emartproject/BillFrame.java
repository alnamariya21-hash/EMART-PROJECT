package emartproject;

import javax.swing.*;
import java.awt.*;

public class BillFrame extends JFrame {

    public BillFrame(String customerName, String items, double total) {
        setTitle("E-Mart - Bill");
        setSize(520, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("E-MART BILL", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 8, 10));
        add(title, BorderLayout.NORTH);

        JTextArea bill = new JTextArea();
        bill.setEditable(false);
        bill.setLineWrap(true);
        bill.setWrapStyleWord(true);
        bill.setFont(new Font("Monospaced", Font.PLAIN, 14));
        bill.setText(
                "             E-MART STORE\n"
                + "----------------------------------\n"
                + "Customer: " + customerName + "\n"
                + "Items: " + items + "\n"
                + "----------------------------------\n"
                + String.format("TOTAL: $%.2f\n", total)
                + "Checkout recorded successfully.\n"
                + "Thank you for shopping!"
        );

        bill.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(new JScrollPane(bill), BorderLayout.CENTER);

        JButton closeButton = new JButton("Return to Store");
        closeButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(closeButton);
        add(buttons, BorderLayout.SOUTH);

        setVisible(true);
    }
}
