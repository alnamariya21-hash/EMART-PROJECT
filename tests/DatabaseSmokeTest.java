import emartproject.*;

/** Headless MySQL integration smoke test. Runs against disposable CI MySQL only. */
public class DatabaseSmokeTest {
    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    public static void main(String[] args) throws Exception {
        Database.initialize();
        DataStore.loadAll();
        require(DataStore.findProduct("P101") != null, "Initial products not loaded");
        require(DataStore.findProduct("P101").getCategory() == ProductCategory.ELECTRONICS,
                "Initial category was not persisted");

        String id = "JDBC_TEST_P1";
        DataStore.addProduct(new Product(id, "Grocery Test Product", 23.50, ProductCategory.GROCERY));
        DataStore.loadProducts();
        Product p = DataStore.findProduct(id);
        require(p != null && p.getCategory() == ProductCategory.GROCERY, "Product/category roundtrip failed");

        Database.register("testbuyer", "strongpass123".toCharArray(), "Customer");
        require(Database.authenticate("testbuyer", "strongpass123".toCharArray(), "Customer"), "Login failed");
        require(!Database.authenticate("testbuyer", "wrongpass".toCharArray(), "Customer"), "Bad password accepted");

        DataStore.selectCustomer("testbuyer");
        DataStore.addToCart(p);
        DataStore.loadCart();
        require(DataStore.cart.size() == 1, "Cart wasn't persisted");
        DataStore.CheckoutReceipt receipt = DataStore.checkout();
        require(receipt.total == 23.50 && receipt.items.contains("Grocery Test Product"), "Receipt incorrect");
        DataStore.loadCart();
        require(DataStore.cart.isEmpty(), "Checkout didn't clear the cart");
        DataStore.loadSales();
        require(!DataStore.sales.isEmpty() && "testbuyer".equals(DataStore.sales.get(0)[0]),
                "Sale not stored");

        DataStore.addEmployee("JDBC_TEST_E1", "Test Employee", "Cashier");
        DataStore.loadEmployees();
        require(DataStore.employees.stream().anyMatch(e -> "JDBC_TEST_E1".equals(e[0])), "Employee wasn't persisted");
        DataStore.deleteEmployee("JDBC_TEST_E1");
        DataStore.deleteProduct(id);
        require(DataStore.findProduct(id) == null, "Delete wasn't persisted");
        DataStore.clearSession();
        System.out.println("DATABASE_SMOKE_TEST_PASS");
    }
}
