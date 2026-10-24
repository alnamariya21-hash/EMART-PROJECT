package emartproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

public class CustomerProductsFrame extends JPanel {
    DefaultTableModel model;
    JTable table;
    String customerName;

    private final Map<ProductCategory, DefaultTableModel> categoryModels =
        new EnumMap<>(ProductCategory.class);
    private final Map<ProductCategory, JTable> categoryTables =
        new EnumMap<>(ProductCategory.class);
    private final Map<ProductCategory, JPanel> categoryPanels =
        new EnumMap<>(ProductCategory.class);
    private final JPanel categoryList = new JPanel();

    public CustomerProductsFrame(String customerName, boolean standalone) {
        this.customerName = customerName;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(248, 250, 253));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        categoryList.setLayout(new BoxLayout(categoryList, BoxLayout.Y_AXIS));
        categoryList.setBackground(new Color(248, 250, 253));

        for (ProductCategory category : ProductCategory.values()) {
            DefaultTableModel categoryModel = new DefaultTableModel(
                new String[]{"Image", "ID", "Product Name", "Category", "Price ($)"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            JTable categoryTable = new JTable(categoryModel);
            categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            categoryTable.setRowHeight(52);
            categoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            ProductImageManager.decorateProductTable(categoryTable);

            categoryModels.put(category, categoryModel);
            categoryTables.put(category, categoryTable);
            categoryPanels.put(category, categoryPanel);

            // Keep the original fields available for compatibility.
            if (table == null) {
                table = categoryTable;
                model = categoryModel;
            }

            JPanel categoryPanel = new JPanel(new BorderLayout(0, 6));
            categoryPanel.setBackground(Color.WHITE);
            categoryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 238)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel heading = new JLabel(category.getLabel());
            heading.setFont(new Font("SansSerif", Font.BOLD, 17));
            heading.setForeground(new Color(28, 94, 173));
            categoryPanel.add(heading, BorderLayout.NORTH);

            JScrollPane tableScroll = new JScrollPane(categoryTable);
            tableScroll.setPreferredSize(new Dimension(850, (3 * 52) + 26));
            categoryPanel.add(tableScroll, BorderLayout.CENTER);
            categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            categoryList.add(categoryPanel);
            categoryList.add(Box.createVerticalStrut(12));
        }

        add(new JScrollPane(categoryList), BorderLayout.CENTER);
        refreshTable();

        if (standalone) {
            setVisible(true);
        }
    }

    private void refreshTable() {
        try {
            DataStore.loadProducts();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Database error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (DefaultTableModel categoryModel : categoryModels.values()) {
            categoryModel.setRowCount(0);
        }

        // Start by hiding every category; show only categories containing products.
        for (Component component : categoryList.getComponents()) {
            if (component instanceof JPanel) {
                component.setVisible(false);
            }
        }

        for (Product p : DataStore.products) {
            ProductCategory category = p.getCategory();
            if (category == null) {
                category = ProductCategory.OTHERS;
            }

            DefaultTableModel categoryModel = categoryModels.get(category);
            if (categoryModel != null) {
                categoryModel.addRow(new Object[]{
                    ProductImageManager.getProductIcon(p, 48),
                    p.getId(),
                    p.getName(),
                    category.getLabel(),
                    String.format("%.2f", p.getPrice())
                });
            }
        }

        // Show a category panel only when it contains at least one product.
        for (ProductCategory category : ProductCategory.values()) {
            JPanel panel = categoryPanels.get(category);
            boolean hasProducts = categoryModels.get(category).getRowCount() > 0;
            panel.setVisible(hasProducts);
            int index = categoryList.getComponentZOrder(panel);
            if (index >= 0 && index + 1 < categoryList.getComponentCount()) {
                categoryList.getComponent(index + 1).setVisible(hasProducts);
            }
        }

        // Keep room for at least three product rows in every category.
        // Categories with more than three products scroll inside their own table.
        for (ProductCategory category : ProductCategory.values()) {
            JTable categoryTable = categoryTables.get(category);
            categoryTable.setPreferredScrollableViewportSize(
                new Dimension(850, (3 * 52) + 2)
            );
            categoryTable.setFillsViewportHeight(true);
            categoryTable.revalidate();
        }
        categoryList.revalidate();
        categoryList.repaint();
    }

    public void addToCart() {
        Product selectedProduct = null;

        for (ProductCategory category : ProductCategory.values()) {
            JTable selectedTable = categoryTables.get(category);
            int selectedRow = selectedTable.getSelectedRow();

            if (selectedRow >= 0) {
                DefaultTableModel selectedModel = categoryModels.get(category);
                String id = selectedModel.getValueAt(
                    selectedTable.convertRowIndexToModel(selectedRow), 1
                ).toString();
                selectedProduct = DataStore.findProduct(id);
                break;
            }
        }

        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a product from one of the category tables.");
            return;
        }

        try {
            DataStore.addToCart(selectedProduct);
            JOptionPane.showMessageDialog(this,
                selectedProduct.getName() + " added to cart.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
}