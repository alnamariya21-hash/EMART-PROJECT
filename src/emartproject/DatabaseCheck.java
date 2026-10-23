package emartproject;

/** Headless startup probe for the Windows launcher; no GUI is needed to check credentials. */
public final class DatabaseCheck {
    private DatabaseCheck() {}
    public static void main(String[] args) {
        try {
            Database.initialize();
            DataStore.loadAll();
            System.out.println("EMART_DATABASE_OK");
        } catch (Exception e) {
            System.err.println("Database login/initialization failed: " + e.getMessage());
            System.exit(2);
        }
    }
}
