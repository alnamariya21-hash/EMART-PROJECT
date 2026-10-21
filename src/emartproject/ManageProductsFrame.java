package emartproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageProductsFrame extends JFrame {

    DefaultTableModel model;
    JTable table;
    JTextField idField, nameField, priceField;

    public ManageProductsFrame() {
        setTitle("E-Mart - Manage Products");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Blue header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(28, 94, 173));
        header.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("MANAGE PRODUCTS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Main content
        JPanel content = new JPanel(new BorderLayout(15, 10));
        content.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left: Product details form
        JPanel formPanel = new JPanel(new BorderLayout(8, 15));
        formPanel.setPreferredSize(new Dimension(270, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Product Details"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        idField = new JTextField(15);
        nameField = new JTextField(15);
        priceField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        fields.add(new JLabel("Product ID:"), gbc);

        gbc.gridy = 1;
        fields.add(idField, gbc);

        gbc.gridy = 2;
        fields.add(new JLabel("Product Name:"), gbc);

        gbc.gridy = 3;
        fields.add(nameField, gbc);

        gbc.gridy = 4;
        fields.add(new JLabel("Price:"), gbc);

        gbc.gridy = 5;
        fields.add(priceField, gbc);

        // Compact buttons
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        addButton.setPreferredSize(new Dimension(85, 30));
        deleteButton.setPreferredSize(new Dimension(85, 30));

        JPanel actionButtons = new JPanel(new FlowLayout(
            FlowLayout.CENTER, 8, 8));

        actionButtons.add(addButton);
        actionButtons.add(deleteButton);

        formPanel.add(fields, BorderLayout.NORTH);
        formPanel.add(actionButtons, BorderLayout.CENTER);

        // Right: Product table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 8));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Product List"));

        model = new DefaultTableModel(
            new String[]{"ID", "Product Name", "Price"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(
            JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);

        refreshTable();

        tablePanel.add(
            new JScrollPane(table), BorderLayout.CENTER);

        content.add(formPanel, BorderLayout.WEST);
        content.add(tablePanel, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        // Bottom: Back button
        JButton backButton = new JButton("Back");
        JPanel bottom = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 15, 8));
        bottom.add(backButton);

        add(bottom, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Product p : DataStore.products) {
            model.addRow(new Object[]{
                p.getId(), p.getName(), p.getPrice()
            });
        }
    }

    private void addProduct() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();

            if (id.isEmpty() || name.isEmpty()
                    || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Enter all product details.");
                return;
            }

            double price = Double.parseDouble(priceText);

            if (price < 0) {
                JOptionPane.showMessageDialog(this,
                    "Price cannot be negative.");
                return;
            }

            if (DataStore.findProduct(id) != null) {
                JOptionPane.showMessageDialog(this,
                    "Product ID already exists.");
                return;
            }

            DataStore.products.add(
                new Product(id, name, price));

            refreshTable();

            idField.setText("");
            nameField.setText("");
            priceField.setText("");

            JOptionPane.showMessageDialog(this,
                "Product added successfully.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Enter a valid price.");
        }
    }

    private void deleteProduct() {
        int selected = table.getSelectedRow();

        if (selected == -1) {
            JOptionPane.showMessageDialog(this,
                "Select a product to delete.");
            return;
        }

        String id = model.getValueAt(
            selected, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete the selected product?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.products.removeIf(
                p -> p.getId().equals(id));

            refreshTable();
        }
    }
}
