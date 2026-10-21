package emartproject;

public class Product {
    private String id;
    private String name;
    private double price;
    private ProductCategory category;
    private String customImagePath;

    public Product(String id, String name, double price) {
        this(id, name, price, ProductCategory.OTHERS, null);
    }

    public Product(String id, String name, double price, ProductCategory category) {
        this(id, name, price, category, null);
    }

    public Product(String id, String name, double price, ProductCategory category, String customImagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category == null ? ProductCategory.OTHERS : category;
        this.customImagePath = customImagePath;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public ProductCategory getCategory() { return category; }
    public String getCustomImagePath() { return customImagePath; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(ProductCategory category) {
        this.category = category == null ? ProductCategory.OTHERS : category;
    }
    public void setCustomImagePath(String path) { this.customImagePath = path; }

    @Override public String toString() {
        return id + " - " + name + " ($" + String.format("%.2f", price) + ")";
    }
}
