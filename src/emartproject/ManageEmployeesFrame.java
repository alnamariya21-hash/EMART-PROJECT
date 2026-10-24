package emartproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ManageEmployeesFrame extends JFrame {

    DefaultTableModel model;
    JTable table;
    JTextField idField, nameField, roleField, salaryField;

    public ManageEmployeesFrame() {
        setTitle("E-Mart - Manage Employees");
        setSize(1000, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Blue header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(28, 94, 173));
        header.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("MANAGE EMPLOYEES");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Main content
        JPanel content = new JPanel(new BorderLayout(15, 10));
        content.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left: Employee details
        JPanel formPanel = new JPanel(new BorderLayout(8, 15));
        formPanel.setPreferredSize(new Dimension(270, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Employee Details"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        idField = new JTextField(15);
        nameField = new JTextField(15);
        roleField = new JTextField(15);
        salaryField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        fields.add(new JLabel("Employee ID:"), gbc);

        gbc.gridy = 1;
        fields.add(idField, gbc);

        gbc.gridy = 2;
        fields.add(new JLabel("Employee Name:"), gbc);

        gbc.gridy = 3;
        fields.add(nameField, gbc);

        gbc.gridy = 4;
        fields.add(new JLabel("Role:"), gbc);

        gbc.gridy = 5;
        fields.add(roleField, gbc);

        gbc.gridy = 6;
        fields.add(new JLabel("Salary:"), gbc);

        gbc.gridy = 7;
        fields.add(salaryField, gbc);

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

        // Right: Employee table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 8));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Employee List"));

        model = new DefaultTableModel(
            new String[]{"ID", "Name", "Role", "Salary"}, 0
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
        addButton.addActionListener(e -> addEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());

        // The admin dashboard remains open behind this window.
        // Close this window instead of creating a duplicate dashboard.
        backButton.addActionListener(e -> dispose());

        UITheme.polishWindow(this);
        setVisible(true);
    }

    private void refreshTable() {
        try { DataStore.loadEmployees(); }
        catch (SQLException e) { showDatabaseError(e); return; }
        model.setRowCount(0);

        for (String[] employee : DataStore.employees) {
            model.addRow(employee);
        }
    }

    private void addEmployee() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String role = roleField.getText().trim();
        String salaryText = salaryField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || role.isEmpty() || salaryText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all fields, including salary.");
            return;
        }

        for (String[] employee : DataStore.employees) {
            if (employee[0].equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(this,
                    "Employee ID already exists.");
                return;
            }
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryText);
            if (!Double.isFinite(salary) || salary < 0) {
                JOptionPane.showMessageDialog(this, "Salary must be a valid non-negative number.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid salary amount, for example 25000.");
            return;
        }

        try { DataStore.addEmployee(id, name, role, salary); }
        catch (SQLException e) { showDatabaseError(e); return; }

        refreshTable();

        idField.setText("");
        nameField.setText("");
        roleField.setText("");
        salaryField.setText("");

        JOptionPane.showMessageDialog(this,
            "Employee added successfully.");
    }

    private void showDatabaseError(SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Database error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteEmployee() {
        int selected = table.getSelectedRow();

        if (selected == -1) {
            JOptionPane.showMessageDialog(this,
                "Select an employee to delete.");
            return;
        }

        String id = model.getValueAt(
            selected, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete the selected employee?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DataStore.deleteEmployee(id);
                refreshTable();
            } catch (SQLException e) { showDatabaseError(e); }
        }
    }
}