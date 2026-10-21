package emartproject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;

/** Image loading, optional product-image importing, scaling and thumbnail rendering. */
public final class ProductImageManager {
    private static final long MAX_BYTES = 8L * 1024 * 1024;
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();
    private ProductImageManager() { }

    public static ImageIcon getCategoryIcon(ProductCategory category, int size) {
        if (category == null) category = ProductCategory.OTHERS;
        String key = category.name() + "@" + size;
        if (CACHE.containsKey(key)) return CACHE.get(key);
        try (InputStream in = ProductImageManager.class.getResourceAsStream(category.getResourcePath())) {
            if (in != null) {
                BufferedImage image = ImageIO.read(in);
                if (image != null) {
                    ImageIcon icon = scale(image, size);
                    CACHE.put(key, icon);
                    return icon;
                }
            }
        } catch (IOException ex) {
            System.err.println("Unable to read category icon: " + ex.getMessage());
        }
        if (category != ProductCategory.OTHERS) return getCategoryIcon(ProductCategory.OTHERS, size);
        return scale(new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB), size);
    }

    public static ImageIcon getProductIcon(Product p, int size) {
        String path = p.getCustomImagePath();
        if (path != null && !path.isEmpty()) {
            String key = path + "@" + size;
            if (CACHE.containsKey(key)) return CACHE.get(key);
            try {
                BufferedImage image = ImageIO.read(new File(path));
                if (image != null) {
                    ImageIcon icon = scale(image, size);
                    CACHE.put(key, icon);
                    return icon;
                }
            } catch (IOException | SecurityException ex) {
                System.err.println("Unable to read product image: " + ex.getMessage());
            }
        }
        return getCategoryIcon(p.getCategory(), size);
    }

    public static ImageIcon preview(File file, int size) throws IOException {
        return scale(readValidated(file), size);
    }

    public static String importProductImage(File file, String productId) throws IOException {
        BufferedImage original = readValidated(file);
        BufferedImage output = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double ratio = Math.min(512.0 / original.getWidth(), 512.0 / original.getHeight());
        int width = Math.max(1, (int)Math.round(original.getWidth() * ratio));
        int height = Math.max(1, (int)Math.round(original.getHeight() * ratio));
        g.drawImage(original, (512-width)/2, (512-height)/2, width, height, null);
        g.dispose();
        Path folder = Paths.get(System.getProperty("user.home"), ".emart", "product-images");
        Files.createDirectories(folder);
        String safeId = productId.replaceAll("[^A-Za-z0-9_-]", "_");
        Path target = folder.resolve(safeId + "-" + UUID.randomUUID() + ".png");
        if (!ImageIO.write(output, "png", target.toFile())) throw new IOException("PNG encoder unavailable");
        return target.toAbsolutePath().toString();
    }

    private static BufferedImage readValidated(File file) throws IOException {
        if (file == null || !file.isFile() || Files.size(file.toPath()) > MAX_BYTES)
            throw new IOException("Select an image smaller than 8 MB.");
        BufferedImage image = ImageIO.read(file);
        if (image == null) throw new IOException("Choose a supported PNG, JPG, GIF or BMP image.");
        if (image.getWidth() > 8000 || image.getHeight() > 8000)
            throw new IOException("Image dimensions must not exceed 8000 pixels.");
        return image;
    }

    private static ImageIcon scale(BufferedImage image, int size) {
        Image scaled = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static void decorateProductTable(JTable table) {
        table.setRowHeight(62);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 239, 245));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(220, 236, 255));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(235, 242, 250));
        header.setForeground(new Color(36, 64, 103));
        header.setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(76);
        table.getColumnModel().getColumn(0).setMaxWidth(90);
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, "", selected, focus, row, col);
                setIcon(value instanceof ImageIcon ? (ImageIcon) value : null);
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
    }
}
