package emartproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerProductsFrame extends JPanel {
    DefaultTableModel model;
    JTable table;
    String customerName;

    public CustomerProductsFrame(
        String customerName, boolean standalone) {

    this.customerName = customerName;

    setLayout(new BorderLayout(10, 10));
    setBackground(new Color(248, 250, 253));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    model = new DefaultTableModel(
        new String[]{"Image", "ID", "Product Name", "Category", "Price ($)"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    table = new JTable(model);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ProductImageManager.decorateProductTable(table);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    refreshTable();

    add(new JScrollPane(table), BorderLayout.CENTER);

        if(standalone){
            setVisible(true);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Product p : DataStore.products) {
            model.addRow(new Object[]{
                ProductImageManager.getProductIcon(p, 48),
                p.getId(), p.getName(), p.getCategory().getLabel(),
                String.format("%.2f", p.getPrice())
            });
        }
    }

    public void addToCart() {
        int selected = table.getSelectedRow();

        if (selected == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a product.");
            return;
        }

        String id = model.getValueAt(table.convertRowIndexToModel(selected), 1).toString();
        Product product = DataStore.findProduct(id);

        if (product != null) {
            DataStore.cart.add(product);

            JOptionPane.showMessageDialog(this,
                product.getName() + " added to cart.");
        }
    }
}