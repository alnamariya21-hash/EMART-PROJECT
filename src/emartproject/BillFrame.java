package emartproject;

import javax.swing.*;
import java.awt.*;

public class BillFrame extends JFrame {

    public BillFrame(String customerName, String items, double total) {
        setTitle("E-Mart - Bill");
        setSize(500, 420);
        setMinimumSize(new Dimension(420, 320));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("E-MART BILL", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(12, 8, 4, 8));
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
                + "Items:\n" + items
                + "----------------------------------\n"
                + String.format("TOTAL: $%.2f\n", total)
                + "Please proceed to payment."
        );

        bill.setCaretPosition(0);
        bill.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(new JScrollPane(bill), BorderLayout.CENTER);

        JButton paymentButton = new JButton("Proceed to Payment");
        JButton closeButton = new JButton("Return to Store");

        paymentButton.addActionListener(e ->
            new PaymentFrame(customerName, items, total, this)
        );
        closeButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttons.add(paymentButton);
        buttons.add(closeButton);
        add(buttons, BorderLayout.SOUTH);

        UITheme.polishWindow(this);
        setVisible(true);
    }
}
