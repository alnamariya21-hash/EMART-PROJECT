package emartproject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Direct JDBC/DriverManager connection; credentials come from the launcher or a login prompt. */
public final class Database {
    private static String user = System.getenv("EMART_DB_USER");
    private static String password = System.getenv("EMART_DB_PASSWORD");
    private static final String HOST = System.getenv("EMART_DB_HOST") == null ? "127.0.0.1" : System.getenv("EMART_DB_HOST");
    private static final String PORT = System.getenv("EMART_DB_PORT") == null ? "3306" : System.getenv("EMART_DB_PORT");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/";
    private static final String PARAMS = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Database() {}

    public static void configure(String dbUser, char[] dbPassword) {
        user = dbUser;
        password = new String(dbPassword);
        java.util.Arrays.fill(dbPassword, '\0');
    }

    public static Connection connect() throws SQLException {
        if (user == null || user.trim().isEmpty()) throw new SQLException("MySQL username has not been configured.");
        return DriverManager.getConnection(URL + "emart_db" + PARAMS, user, password == null ? "" : password);
    }

    /** Creates a fresh schema without deleting existing data. Requires CREATE DATABASE permission the first time. */
    public static void initialize() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J is missing. Start E-Mart using run.bat.", e);
        }
        if (user == null || user.trim().isEmpty()) throw new SQLException("MySQL credentials are missing.");
        try (Connection conn = DriverManager.getConnection(URL + PARAMS, user, password == null ? "" : password);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS emart_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        try (Connection conn = connect(); Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                + "username VARCHAR(64) PRIMARY KEY, role VARCHAR(12) NOT NULL,"
                + "password_salt VARCHAR(64) NOT NULL, password_hash VARCHAR(128) NOT NULL,"
                + "full_name VARCHAR(120) NOT NULL DEFAULT '',"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB");
            try (ResultSet columns = conn.getMetaData().getColumns(null, null, "users", "full_name")) {
                if (!columns.next()) {
                    st.executeUpdate("ALTER TABLE users ADD COLUMN full_name VARCHAR(120) NOT NULL DEFAULT ''");
                }
            }
            st.executeUpdate("CREATE TABLE IF NOT EXISTS products ("
                + "id VARCHAR(40) PRIMARY KEY, name VARCHAR(200) NOT NULL,"
                + "price DECIMAL(12,2) NOT NULL, category VARCHAR(24) NOT NULL,"
                + "image_path VARCHAR(1024), CHECK (price >= 0)) ENGINE=InnoDB");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS employees ("
                + "id VARCHAR(40) PRIMARY KEY, name VARCHAR(150) NOT NULL,"
                + "role VARCHAR(120) NOT NULL) ENGINE=InnoDB");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS cart_items ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, customer VARCHAR(64) NOT NULL,"
                + "product_id VARCHAR(40) NOT NULL, "
                + "FOREIGN KEY (customer) REFERENCES users(username) ON DELETE CASCADE,"
                + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE) ENGINE=InnoDB");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS sales ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, customer VARCHAR(64) NOT NULL,"
                + "items TEXT NOT NULL, total DECIMAL(12,2) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS app_meta ("
                + "meta_key VARCHAR(64) PRIMARY KEY, meta_value VARCHAR(255) NOT NULL) ENGINE=InnoDB");
            boolean seedNeeded;
            try (ResultSet rs = st.executeQuery(
                "SELECT COUNT(*) FROM app_meta WHERE meta_key='initial_seed_done'")) {
                rs.next();
                seedNeeded = rs.getLong(1) == 0;
            }
            if (seedNeeded) {
                // Only seed a table on first use. Deleted sample rows must stay deleted.
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
                    rs.next();
                    if (rs.getLong(1) == 0) {
                        try (PreparedStatement seed = conn.prepareStatement(
                            "INSERT INTO products(id,name,price,category) VALUES "
                          + "('P101','Wireless Mouse',25.00,'ELECTRONICS'),"
                          + "('P102','Mechanical Keyboard',75.00,'ELECTRONICS'),"
                          + "('P103','HD Monitor',180.00,'ELECTRONICS')")) { seed.executeUpdate(); }
                    }
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM employees")) {
                    rs.next();
                    if (rs.getLong(1) == 0) {
                        try (PreparedStatement seed = conn.prepareStatement(
                            "INSERT INTO employees(id,name,role) VALUES "
                          + "('E101','Alex','Sales Assistant'),('E102','Maya','Store Manager')")) { seed.executeUpdate(); }
                    }
                }
    
                st.executeUpdate("INSERT INTO app_meta(meta_key,meta_value) VALUES "
                    + "('initial_seed_done','yes')");
            }
        }
        ensureDefaultAdmin();
    }

    private static void ensureDefaultAdmin() throws SQLException {
        try (Connection conn = connect();
             PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role='Admin'")) {
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) != 0) return;
            }
        }
        try { register("admin", "admin123".toCharArray(), "Admin"); }
        catch (SQLException e) {
            // Pre-existing 'admin' username is a configuration conflict, not a reason to reset credentials.
            if (e.getSQLState() == null || !e.getSQLState().startsWith("23")) throw e;
        }
    }

    public static void register(String username, char[] rawPassword, String role) throws SQLException {
        register(username, username, rawPassword, role);
    }

    public static void register(String username, String fullName, char[] rawPassword, String role) throws SQLException {
        if (username == null || !username.matches("[A-Za-z0-9_.-]{3,64}"))
            throw new SQLException("Username must be 3-64 letters, numbers or _ . -");
        if (fullName == null || fullName.trim().isEmpty() || fullName.trim().length() > 120)
            throw new SQLException("Enter your full name (up to 120 characters).");
        if (rawPassword == null || rawPassword.length < 6)
            throw new SQLException("Password must have at least 6 characters.");
        if (!"Admin".equals(role) && !"Customer".equals(role))
            throw new SQLException("Unsupported account role.");
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String saltText = Base64.getEncoder().encodeToString(salt);
        String hash = hash(rawPassword, salt);
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO users(username,role,password_salt,password_hash,full_name) VALUES (?,?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, saltText);
            ps.setString(4, hash);
            ps.setString(5, fullName.trim());
            ps.executeUpdate();
        }
    }

    public static String getFullName(String username) throws SQLException {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT full_name FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString(1);
                    if (name != null && !name.trim().isEmpty()) return name.trim();
                }
            }
        }
        return username;
    }

    public static boolean authenticate(String username, char[] rawPassword, String role) throws SQLException {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT password_salt,password_hash FROM users WHERE username=? AND role=?")) {
            ps.setString(1, username);
            ps.setString(2, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                byte[] salt = Base64.getDecoder().decode(rs.getString(1));
                String expected = rs.getString(2);
                String actual = hash(rawPassword, salt);
                return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                                             actual.getBytes(StandardCharsets.UTF_8));
            }
        } finally {
            if (rawPassword != null) java.util.Arrays.fill(rawPassword, '\0');
        }
    }

    private static String hash(char[] password, byte[] salt) throws SQLException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 120000, 256);
        try {
            byte[] bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new SQLException("Password hashing unavailable.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
