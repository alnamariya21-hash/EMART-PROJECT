package emartproject;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("E-Mart - Admin Dashboard");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 34, 40));
        header.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("E-MART ADMIN PANEL");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel welcome = new JLabel("Welcome, admin");
        welcome.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(welcome, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Dashboard buttons
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBorder(
                BorderFactory.createEmptyBorder(35, 35, 35, 35));
        grid.setBackground(new Color(245, 247, 250));

        JButton products = makeButton("Manage Products");
        JButton employees = makeButton("Manage Employees");
        JButton sales = makeButton("View Sales Orders");
        JButton logout = makeButton("Logout");

        products.addActionListener(
                e -> new ManageProductsFrame());

        employees.addActionListener(
                e -> new ManageEmployeesFrame());

        sales.addActionListener(
                e -> new ViewSalesFrame());

        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        grid.add(products);
        grid.add(employees);
        grid.add(sales);
        grid.add(logout);

        add(grid, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setBackground(Color.WHITE);
        return button;
    }
}
