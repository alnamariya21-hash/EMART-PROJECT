package emartproject;

import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    private final String customerName;
    private final double total;
    private final JFrame billFrame;
    private final JComboBox<String> methodBox;
    private final JTextField upiField;
    private final JLabel upiLabel;

    public PaymentFrame(String customerName, String items, double total, JFrame billFrame) {
        this.customerName = customerName;
        this.total = total;
        this.billFrame = billFrame;

        setTitle("E-Mart - Payment");
        setSize(460, 360);
        setMinimumSize(new Dimension(420, 320));
        setLocationRelativeTo(billFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JLabel heading = new JLabel("PAYMENT", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        main.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        JLabel amountLabel = new JLabel(String.format("Amount to pay: $%.2f", total));
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        form.add(amountLabel);

        form.add(new JLabel("Select payment method:"));
        methodBox = new JComboBox<>(new String[]{"UPI", "Cash", "Card"});
        form.add(methodBox);

        upiLabel = new JLabel("Enter UPI ID / number:");
        upiField = new JTextField();
        form.add(upiLabel);
        form.add(upiField);

        methodBox.addActionListener(e -> updateUpiFields());
        updateUpiFields();
        main.add(form, BorderLayout.CENTER);

        JButton payButton = new JButton("Pay Now");
        JButton cancelButton = new JButton("Back to Bill");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.add(payButton);
        buttons.add(cancelButton);
        main.add(buttons, BorderLayout.SOUTH);

        payButton.addActionListener(e -> completePayment());
        cancelButton.addActionListener(e -> dispose());

        add(main);
        setVisible(true);
    }

    private void updateUpiFields() {
        boolean isUpi = "UPI".equals(methodBox.getSelectedItem());
        upiLabel.setVisible(isUpi);
        upiField.setVisible(isUpi);
        revalidate();
        repaint();
    }

    private void completePayment() {
        String method = (String) methodBox.getSelectedItem();

        if ("UPI".equals(method)) {
            String upi = upiField.getText().trim();
            if (!upi.matches("[A-Za-z0-9._-]+@[A-Za-z0-9._-]+")) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid UPI ID, for example name@bank.",
                    "Invalid UPI ID", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // This is a demo payment confirmation; it does not transfer real money.
        String message = "E-MART\n\n"
            + "Payment Successful!\n\n"
            + "Customer: " + customerName + "\n"
            + String.format("Amount: $%.2f\n", total)
            + "Payment method: " + method + "\n\n"
            + "Thank you for shopping!";
        JOptionPane.showMessageDialog(this, message, "E-Mart",
            JOptionPane.INFORMATION_MESSAGE);

        if (billFrame != null) {
            billFrame.dispose();
        }
        dispose();
    }
}
