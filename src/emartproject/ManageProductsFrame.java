package emartproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ManageProductsFrame extends JFrame {

    DefaultTableModel model;
    JTable table;
    JTextField idField, nameField, priceField;
    private JComboBox<ProductCategory> categoryBox;
    private JLabel imagePreview;
    private JLabel imageHint;
    private File selectedImageFile;

    public ManageProductsFrame() {
        setTitle("E-Mart - Manage Products");
        setSize(1070, 630);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Blue header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(29, 68, 119));
        header.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("MANAGE PRODUCTS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("  Categories + product images");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(205, 225, 249));
        header.add(subtitle, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Main content
        JPanel content = new JPanel(new BorderLayout(15, 10));
        content.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left: Product details form
        JPanel formPanel = new JPanel(new BorderLayout(8, 15));
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Product Details"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
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

        categoryBox = new JComboBox<>(ProductCategory.values());
        categoryBox.setSelectedItem(ProductCategory.ELECTRONICS);
        UITheme.styleCategoryComboBox(categoryBox);
        gbc.gridy = 6;
        fields.add(new JLabel("Category:"), gbc);
        gbc.gridy = 7;
        fields.add(categoryBox, gbc);

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

        JPanel imagePanel = new JPanel(new BorderLayout(8, 5));
        imagePanel.setBorder(BorderFactory.createTitledBorder("Product Image"));
        imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(88, 88));
        imagePreview.setOpaque(true);
        imagePreview.setBackground(new Color(239, 246, 253));
        imagePanel.add(imagePreview, BorderLayout.WEST);
        JPanel imageActions = new JPanel();
        imageActions.setLayout(new BoxLayout(imageActions, BoxLayout.Y_AXIS));
        JButton importButton = new JButton("Import Image...");
        JButton resetButton = new JButton("Use Category Icon");
        importButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageHint = new JLabel("Default category icon");
        imageHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        imageActions.add(importButton);
        imageActions.add(Box.createVerticalStrut(6));
        imageActions.add(resetButton);
        imageActions.add(Box.createVerticalStrut(8));
        imageActions.add(imageHint);
        imagePanel.add(imageActions, BorderLayout.CENTER);
        formPanel.add(imagePanel, BorderLayout.CENTER);
        formPanel.add(actionButtons, BorderLayout.SOUTH);

        categoryBox.addActionListener(e -> updateImagePreview());
        importButton.addActionListener(e -> chooseImage());
        resetButton.addActionListener(e -> {
            selectedImageFile = null;
            updateImagePreview();
        });

        // Right: Product table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 8));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Product List"));

        model = new DefaultTableModel(
            new String[]{"Image", "ID", "Product Name", "Category", "Price ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        ProductImageManager.decorateProductTable(table);
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

        backButton.addActionListener(e -> dispose());
        updateImagePreview();
        UITheme.polishWindow(this);
        setVisible(true);
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a product image");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Images (*.png, *.jpg, *.jpeg, *.gif, *.bmp)",
            "png", "jpg", "jpeg", "gif", "bmp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File chosen = chooser.getSelectedFile();
        try {
            ImageIcon preview = ProductImageManager.preview(chosen, 76);
            selectedImageFile = chosen;
            imagePreview.setIcon(preview);
            imageHint.setText("Custom image selected");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Cannot open image", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateImagePreview() {
        if (imagePreview == null || selectedImageFile != null) return;
        imagePreview.setIcon(ProductImageManager.getCategoryIcon(
            (ProductCategory) categoryBox.getSelectedItem(), 76));
        imageHint.setText("Default category icon");
    }

    private void refreshTable() {
        try { DataStore.loadProducts(); }
        catch (SQLException e) { showDatabaseError(e); return; }
        model.setRowCount(0);

        for (Product p : DataStore.products) {
            model.addRow(new Object[]{
                ProductImageManager.getProductIcon(p, 48),
                p.getId(), p.getName(), p.getCategory().getLabel(),
                String.format("%.2f", p.getPrice())
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

            if (!Double.isFinite(price) || price < 0) {
                JOptionPane.showMessageDialog(this,
                    "Price cannot be negative.");
                return;
            }

            if (DataStore.findProduct(id) != null) {
                JOptionPane.showMessageDialog(this,
                    "Product ID already exists.");
                return;
            }

            String customPath = null;
            if (selectedImageFile != null) {
                customPath = ProductImageManager.importProductImage(selectedImageFile, id);
            }
            DataStore.addProduct(
                new Product(id, name, price,
                    (ProductCategory) categoryBox.getSelectedItem(), customPath));

            refreshTable();

            idField.setText("");
            nameField.setText("");
            priceField.setText("");
            selectedImageFile = null;
            updateImagePreview();

            JOptionPane.showMessageDialog(this,
                "Product added successfully.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Enter a valid price.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Image import failed", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showDatabaseError(SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Database error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteProduct() {
        int selected = table.getSelectedRow();

        if (selected == -1) {
            JOptionPane.showMessageDialog(this,
                "Select a product to delete.");
            return;
        }

        String id = model.getValueAt(
            table.convertRowIndexToModel(selected), 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete the selected product?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DataStore.deleteProduct(id);
                refreshTable();
            } catch (SQLException ex) { showDatabaseError(ex); }
        }
    }
}
