package emartproject;

/** Six store categories, each with its own packaged PNG icon. */
public enum ProductCategory {
    ELECTRONICS("Electronics", "electronics.png"),
    GROCERY("Grocery", "grocery.png"),
    SPORTS("Sports", "sports.png"),
    CLOTHES("Clothes", "clothes.png"),
    STATIONERY("Stationery", "stationery.png"),
    OTHERS("Others", "others.png");

    private final String label;
    private final String iconFile;

    ProductCategory(String label, String iconFile) {
        this.label = label;
        this.iconFile = iconFile;
    }

    public String getLabel() { return label; }
    public String getResourcePath() { return "/emartproject/images/" + iconFile; }

    @Override public String toString() { return label; }
}
