package emartproject;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    private String customerName;
    private CustomerProductsFrame productsFrame;

    public CustomerDashboard(String customerName) {
        this.customerName = customerName;

        setTitle("E-Mart - Customer Shop");
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(28, 94, 173));
        header.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("E-MART STORE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel welcome = new JLabel(
                "Welcome, " + customerName);
        welcome.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(welcome, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Product table
        productsFrame = new CustomerProductsFrame(
        customerName, false);
        
        add(productsFrame, BorderLayout.CENTER);


        // Bottom buttons
        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));

        JButton addToCart = new JButton("Add to Cart");
        JButton cart = new JButton("View Cart");
        JButton logout = new JButton("Logout");

        addToCart.addActionListener(
                e -> productsFrame.addToCart());

        cart.addActionListener(
                e -> new CartFrame(customerName));

        logout.addActionListener(e -> {
            DataStore.clearSession();
            dispose();
            new LoginFrame();
        });
   

        buttons.add(addToCart);
        buttons.add(cart);
        buttons.add(logout);

        add(buttons, BorderLayout.SOUTH);



        setVisible(true);
    }
}
