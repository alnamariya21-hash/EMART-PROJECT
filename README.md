# E-Mart — Java Swing store demo

E-Mart is a Java Swing shopping/store-management demonstration with customer/admin dashboards, a cart, a bill screen, employee management and sales history.

## Category images

Products now have one of six categories: **Electronics, Grocery, Sports, Clothes, Stationery, Others**. Each category has an actual PNG illustration in `src/emartproject/images/`. Icons appear in the admin product table, customer product list and cart.

In **Admin → Manage Products**, pick a category. The form previews its default icon. You can also choose **Import Image...** to select a specific product image (PNG/JPG/GIF/BMP), with an immediate preview; selecting **Use Category Icon** resets to the default. Imported images are resized, converted to PNG and saved under `~/.emart/product-images/`. A missing imported image falls back to the corresponding category icon.

**Known original behavior:** Product records and their custom-image associations live only in memory and reset when the application exits. The import itself writes a PNG file to disk, but adding database persistence is deliberately outside this small change.

## Build and run

A Java 8+ JDK is required.

- **Windows:** Run `build.bat`, then `java -jar dist\EMartProject.jar`
- **macOS/Linux:** Run `bash build.sh`, then `java -jar dist/EMartProject.jar`

The build scripts package the real PNG resources in the JAR. If compiling in an IDE, ensure the resources are present at `emartproject/images/*.png` on the runtime classpath. Rebuild the JAR before running: the previously committed JAR predates this feature.
