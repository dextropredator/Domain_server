package utils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MimeTypes {
    private static final Map<String, String> MIME_MAP = new HashMap<>();

    static {
        // Text types
        MIME_MAP.put("html", "text/html; charset=utf-8");
        MIME_MAP.put("htm",  "text/html; charset=utf-8");
        MIME_MAP.put("css",  "text/css");
        MIME_MAP.put("js",   "application/javascript");
        MIME_MAP.put("txt",  "text/plain");

        // Image types
        MIME_MAP.put("png",  "image/png");
        MIME_MAP.put("jpg",  "image/jpeg");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("gif",  "image/gif");
        MIME_MAP.put("ico",  "image/x-icon");
        MIME_MAP.put("svg",  "image/svg+xml");

        // Application types
        MIME_MAP.put("json", "application/json");
        MIME_MAP.put("pdf",  "application/pdf");
    }

    /**
     * Guesses the MIME type based on the file extension.
     */
    public static String getMimeType(Path path) {
        String filename = path.getFileName().toString().toLowerCase();
        int dotIndex = filename.lastIndexOf('.');
        
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            String extension = filename.substring(dotIndex + 1);
            return MIME_MAP.getOrDefault(extension, "application/octet-stream");
        }
        
        return "application/octet-stream"; // Default for unknown types
    }
}