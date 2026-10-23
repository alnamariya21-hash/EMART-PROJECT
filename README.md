# E-Mart — Java Swing + MySQL (JDBC)

E-Mart is a college Java Swing project with customer/admin login, product categories with **real PNG images**, optional custom product images, employee management, a persistent shopping cart and sales records.

## Windows: quick start (VRMS-style; no manual Connector/J JAR)

**Install first:** A Java JDK 8 or later and a local **MySQL Server 8.0+** (MySQL Workbench is optional, and is not the database server).

1. Clone or pull this repository.
2. Start the local MySQL service. The launcher tries to start an installed `MySQL*` Windows service if it is stopped; Windows permissions may prevent this.
3. From the repository directory, run **`run.bat`** (double-click or in PowerShell: `.\run.bat`).
4. On first run, enter your **local MySQL username/password** (usually `root` and the password created during MySQL installation).
5. The launcher automatically downloads MySQL Connector/J **9.4.0** from Maven Central into `lib/`, compiles Java, includes the six PNG resources in the JAR and starts the app. The app creates `emart_db` and tables automatically if the account has permission.

**No manually installing a connector JAR or importing SQL is necessary.** The first launch requires internet access to fetch the driver; subsequent launches reuse it.

Local MySQL credentials are remembered only for the current Windows account: password protected using Windows DPAPI at `%LOCALAPPDATA%\EMart\mysql.secret`, username at `%LOCALAPPDATA%\EMart\mysql.user`. Neither file is committed to Git. Delete both to make the launcher ask again if you changed your MySQL credentials.

### App login after MySQL is configured

- **Admin:** username `admin`, password `admin123` (demo-only starter account).
- **Customer:** enter a username and password (6+ characters) and click **Register New Customer** on first use, then log in as Customer.
- The admin user is seeded only if no admin exists. This starter password is for a local classroom demo; do not deploy it as-is.

### What persists now?

| Feature | MySQL table |
|---|---|
| Customer and admin accounts (salted PBKDF2 hashes) | `users` |
| Product name, price, category and custom-image file path | `products` |
| Employee records | `employees` |
| Each customer's shopping cart | `cart_items` |
| Completed demo checkouts | `sales` |

Checkout writes a sale and clears its cart in **one JDBC transaction**. Everything above survives an app restart. Custom images are imported as files under `~/.emart/product-images/` and their **paths** are persisted in MySQL; they remain local to the importing computer. Other machines automatically display the category PNG as a fallback.

Payment is **not processed** by this classroom demo. A bill means the checkout record was saved.

### Using NetBeans

NetBeans can edit the classes normally, but running the main class from an ordinary *Java Project with Existing Sources* does **not** automatically add the MySQL JDBC driver to that project's classpath. For the least confusing demo, open the repository folder in NetBeans for editing, then run `run.bat` from that folder in a terminal. No NetBeans library configuration is required that way. If she prefers NetBeans F6, she must configure its project libraries to include the automatically downloaded `lib/mysql-connector-j-9.4.0.jar`.

The main class is `emartproject.EMartProject`. `build.bat` builds the app without launching it. `build.sh` is the equivalent build script for macOS/Linux. Run with `java -jar dist/EMartProject.jar` after building; the JAR manifest references Connector/J in the neighboring `lib/` directory.

### Manual database inspection (optional)

```sql
USE emart_db;
SELECT id, name, category, price FROM products;
SELECT id, name, role FROM employees;
SELECT customer, items, total, created_at FROM sales ORDER BY id DESC;
SELECT customer, product_id FROM cart_items;
```

### Validation

GitHub Actions runs a headless MySQL 8 integration check after compiling and packaging the application. It exercises customer registration, login, product categories, add/delete operations, persistent carts, checkout and sales. The GUI is still best verified on the actual Windows machine.

### Constraints

- Intended for a **local educational demonstration**, not an internet-exposed production storefront.
- The default `admin/admin123` must be replaced with an admin password-management flow before any deployment beyond the classroom.
- Database creation requires an account with `CREATE DATABASE` privileges on first setup.
