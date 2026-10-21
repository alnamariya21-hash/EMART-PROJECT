package emartproject;

import java.util.ArrayList;

public class DataStore {
    public static ArrayList<Product> products = new ArrayList<>();
    public static ArrayList<Product> cart = new ArrayList<>();
    public static ArrayList<String[]> employees = new ArrayList<>();
    public static ArrayList<String[]> sales = new ArrayList<>();

    static {
        products.add(new Product("P101", "Wireless Mouse", 25.0, ProductCategory.ELECTRONICS));
        products.add(new Product("P102", "Mechanical Keyboard", 75.0, ProductCategory.ELECTRONICS));
        products.add(new Product("P103", "HD Monitor", 180.0, ProductCategory.ELECTRONICS));
        employees.add(new String[]{"E101", "Alex", "Sales Assistant"});
        employees.add(new String[]{"E102", "Maya", "Store Manager"});
    }

    public static Product findProduct(String id) {
        for (Product p : products) if (p.getId().equals(id)) return p;
        return null;
    }

    public static double getCartTotal() {
        double total = 0;
        for (Product p : cart) total += p.getPrice();
        return total;
    }
}
