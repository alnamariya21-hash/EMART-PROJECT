package emartproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewSalesFrame extends JFrame {

    private DefaultTableModel model;

    public ViewSalesFrame() {
        setTitle("E-Mart - Sales Orders");
        setSize(750, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel(
                "SALES ORDERS", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Customer", "Items", "Total ($)"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        refreshTable();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");

        refreshButton.addActionListener(e -> refreshTable());

        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        JPanel buttons = new JPanel();
        buttons.add(refreshButton);
        buttons.add(backButton);

        add(buttons, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (String[] sale : DataStore.sales) {
            model.addRow(sale);
        }
    }
}
