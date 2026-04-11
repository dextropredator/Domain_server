package utils;

import java.io.IOException;
import java.nio.file.*;


public class FileLoader {
    private static Path rootPath = null;

    
    public static Path resolveFile(String wwwRoot, String requestPath) {
        try {
            if (rootPath == null) {
                rootPath = Paths.get(wwwRoot).toAbsolutePath().normalize();
            }
            if (requestPath == null || requestPath.contains("\0")) return null;

            
            int q = requestPath.indexOf('?');
            if (q >= 0) requestPath = requestPath.substring(0, q);

            if (!requestPath.startsWith("/")) requestPath = "/" + requestPath;

            Path resolved = rootPath.resolve(requestPath.substring(1)).normalize();

            
            if (!resolved.startsWith(rootPath)) {
                Logger.warning("Blocked directory traversal attempt: " + requestPath);
                return null;
            }

            if (Files.exists(resolved) && Files.isRegularFile(resolved) && Files.isReadable(resolved)) {
                return resolved;
            } else {
                return null;
            }
        } catch (InvalidPathException e) {
            return null;
        }
    }

    public static String getAbsoluteRootPath(String wwwRoot) {
        try {
            return Paths.get(wwwRoot).toAbsolutePath().normalize().toString();
        } catch (Exception e) {
            return wwwRoot;
        }
    }

    /**
     * Check for custom 404 page (www/404.html)
     */
    public static Path getCustom404(String wwwRoot) {
        try {
            Path root = Paths.get(wwwRoot).toAbsolutePath().normalize();
            Path p = root.resolve("404.html").normalize();
            if (p.startsWith(root) && Files.exists(p) && Files.isRegularFile(p)) {
                return p;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}