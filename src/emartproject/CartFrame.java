package emartproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class CartFrame extends JFrame {
    DefaultTableModel model;
    JTable table;
    String customerName;

    public CartFrame(String customerName) {
        this.customerName = customerName;

        setTitle("Shopping Cart");
        setSize(760, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        model = new DefaultTableModel(
            new String[]{"Image", "ID", "Product Name", "Category", "Price ($)"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ProductImageManager.decorateProductTable(table);

        JLabel totalLabel = new JLabel();
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setForeground(new Color(0, 150, 0));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        totalLabel.setBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 20));

        JButton removeButton = new JButton("Remove Item");
        JButton checkoutButton = new JButton("Checkout");
        JButton backButton = new JButton("Back");

        JPanel buttons = new JPanel(
        new FlowLayout(FlowLayout.RIGHT));

        buttons.add(removeButton);
        buttons.add(checkoutButton);
        buttons.add(backButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttons, BorderLayout.SOUTH);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        Runnable refresh = () -> {
            model.setRowCount(0);

            for (Product p : DataStore.cart) {
                model.addRow(new Object[]{
                    ProductImageManager.getProductIcon(p, 48),
                    p.getId(), p.getName(), p.getCategory().getLabel(),
                    String.format("%.2f", p.getPrice())
                });
            }

            totalLabel.setText(
                "Total: $" + String.format("%.2f",
                    DataStore.getCartTotal())
            );
        };

        try { DataStore.loadCart(); }
        catch (SQLException ex) { showDatabaseError(ex); }
        refresh.run();

        removeButton.addActionListener(e -> {
            int selected = table.getSelectedRow();

            if (selected == -1) {
                JOptionPane.showMessageDialog(this,
                    "Select an item to remove.");
                return;
            }

            try {
                DataStore.removeCartItem(table.convertRowIndexToModel(selected));
                refresh.run();
            } catch (SQLException ex) { showDatabaseError(ex); }
        });

        checkoutButton.addActionListener(e -> {
            if (DataStore.cart.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Your cart is empty.");
                return;
            }

            try {
                DataStore.CheckoutReceipt receipt = DataStore.checkout();
                dispose();
                new BillFrame(receipt.customer, receipt.items, receipt.total);
                if (!receipt.getRefreshWarning().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                        "Your checkout was saved successfully, but some screen data could not refresh. "
                        + "Reopen the relevant screen to reload it.\\n\\n"
                        + receipt.getRefreshWarning(),
                        "Checkout completed with refresh warning",
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) { showDatabaseError(ex); }
        });

        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void showDatabaseError(SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    }
}