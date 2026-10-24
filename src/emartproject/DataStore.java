package emartproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Small JDBC-backed data facade that preserves the original Swing table interfaces. */
public final class DataStore {
    public static final ArrayList<Product> products = new ArrayList<>();
    public static final ArrayList<Product> cart = new ArrayList<>();
    public static final ArrayList<String[]> employees = new ArrayList<>();
    public static final ArrayList<String[]> sales = new ArrayList<>();
    private static final ArrayList<Long> cartIds = new ArrayList<>();
    private static String activeCustomer;

    private DataStore() {}

    public static void loadAll() throws SQLException {
        loadProducts();
        loadEmployees();
        loadSales();
    }

    public static void loadProducts() throws SQLException {
        ArrayList<Product> found = new ArrayList<>();
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT id,name,price,category,image_path FROM products ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProductCategory category;
                try { category = ProductCategory.valueOf(rs.getString("category")); }
                catch (IllegalArgumentException ex) { category = ProductCategory.OTHERS; }
                found.add(new Product(rs.getString("id"), rs.getString("name"),
                    rs.getDouble("price"), category, rs.getString("image_path")));
            }
        }
        products.clear();
        products.addAll(found);
    }

    public static void addProduct(Product p) throws SQLException {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO products(id,name,price,category,image_path) VALUES(?,?,?,?,?)")) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getCategory().name());
            ps.setString(5, p.getCustomImagePath());
            ps.executeUpdate();
        }
        loadProducts();
    }

    public static void deleteProduct(String id) throws SQLException {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
        loadProducts();
    }

    public static Product findProduct(String id) {
        for (Product p : products) if (p.getId().equals(id)) return p;
        return null;
    }

    public static void loadEmployees() throws SQLException {
        ArrayList<String[]> found = new ArrayList<>();
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT id,name,role FROM employees ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) found.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
        }
        employees.clear();
        employees.addAll(found);
    }

    public static void addEmployee(String id, String name, String role) throws SQLException {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO employees(id,name,role) VALUES(?,?,?)")) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, role);
            ps.executeUpdate();
        }
        loadEmployees();
    }

    public static void deleteEmployee(String id) throws SQLException {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
        loadEmployees();
    }

    public static void loadSales() throws SQLException {
        ArrayList<String[]> found = new ArrayList<>();
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT customer,items,total FROM sales ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) found.add(new String[]{
                rs.getString(1), rs.getString(2), String.format("%.2f", rs.getDouble(3))
            });
        }
        sales.clear();
        sales.addAll(found);
    }

    /** One persistent cart per registered customer; old in-memory shared-cart behavior is removed. */
    public static void selectCustomer(String username) throws SQLException {
        activeCustomer = username;
        loadCart();
    }

    public static void clearSession() {
        activeCustomer = null;
        cart.clear();
        cartIds.clear();
    }

    public static void loadCart() throws SQLException {
        ArrayList<Product> found = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        if (activeCustomer != null) {
            try (Connection conn = Database.connect();
                 PreparedStatement ps = conn.prepareStatement(
                    "SELECT c.id,p.id,p.name,p.price,p.category,p.image_path "
                  + "FROM cart_items c JOIN products p ON p.id=c.product_id "
                  + "WHERE c.customer=? ORDER BY c.id")) {
                ps.setString(1, activeCustomer);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ProductCategory category;
                        try { category = ProductCategory.valueOf(rs.getString(5)); }
                        catch (IllegalArgumentException e) { category = ProductCategory.OTHERS; }
                        ids.add(rs.getLong(1));
                        found.add(new Product(rs.getString(2), rs.getString(3),
                            rs.getDouble(4), category, rs.getString(6)));
                    }
                }
            }
        }
        cart.clear();
        cart.addAll(found);
        cartIds.clear();
        cartIds.addAll(ids);
    }

    public static void addToCart(Product product) throws SQLException {
        if (activeCustomer == null) throw new SQLException("Please log in before adding to cart.");
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO cart_items(customer,product_id) VALUES(?,?)")) {
            ps.setString(1, activeCustomer);
            ps.setString(2, product.getId());
            ps.executeUpdate();
        }
        loadCart();
    }

    public static void removeCartItem(int index) throws SQLException {
        if (activeCustomer == null || index < 0 || index >= cartIds.size())
            throw new SQLException("Invalid cart selection.");
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM cart_items WHERE id=? AND customer=?")) {
            ps.setLong(1, cartIds.get(index));
            ps.setString(2, activeCustomer);
            ps.executeUpdate();
        }
        loadCart();
    }

    public static double getCartTotal() {
        double total = 0;
        for (Product p : cart) total += p.getPrice();
        return total;
    }

    /** Atomic sale creation + cart deletion; never records a sale without finishing checkout. */
    public static CheckoutReceipt checkout() throws SQLException {
        if (activeCustomer == null) throw new SQLException("Please log in first.");
        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);
            try {
                StringBuilder items = new StringBuilder();
                java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.name,p.price FROM cart_items c "
                  + "JOIN products p ON p.id=c.product_id "
                  + "WHERE c.customer=? ORDER BY c.id FOR UPDATE")) {
                    ps.setString(1, activeCustomer);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            java.math.BigDecimal price = rs.getBigDecimal(2);
                            total = total.add(price);
                            items.append(rs.getString(1))
                                .append(" - $").append(price.toPlainString()).append("\n");
                        }
                    }
                }
                if (items.length() == 0) throw new SQLException("Your cart is empty.");
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sales(customer,items,total) VALUES(?,?,?)")) {
                    ps.setString(1, activeCustomer);
                    ps.setString(2, items.toString().replace("\n", ", "));
                    ps.setBigDecimal(3, total);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM cart_items WHERE customer=?")) {
                    ps.setString(1, activeCustomer);
                    ps.executeUpdate();
                }
                conn.commit();
                CheckoutReceipt receipt = new CheckoutReceipt(activeCustomer, items.toString(), total.doubleValue());
                // The sale is committed. Do not report checkout failure if refreshing
                // the in-memory cart/sales views fails afterwards.
                try {
                    loadCart();
                } catch (SQLException refreshError) {
                    receipt.addRefreshWarning("Cart refresh failed: " + refreshError.getMessage());
                }
                try {
                    loadSales();
                } catch (SQLException refreshError) {
                    receipt.addRefreshWarning("Sales history refresh failed: " + refreshError.getMessage());
                }
                return receipt;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public static final class CheckoutReceipt {
        public final String customer;
        public final String items;
        public final double total;
        private String refreshWarning = "";

        public CheckoutReceipt(String customer, String items, double total) {
            this.customer = customer;
            this.items = items;
            this.total = total;
        }

        private void addRefreshWarning(String warning) {
            if (refreshWarning.isEmpty()) refreshWarning = warning;
            else refreshWarning += "\\n" + warning;
        }

        public String getRefreshWarning() {
            return refreshWarning;
        }
    }
}
