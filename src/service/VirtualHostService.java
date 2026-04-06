package service;

import model.HttpRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;

public class VirtualHostService {

    public static void serve(HttpRequest request, OutputStream out) throws IOException {
        String method = request.getMethod();
        String uri = request.getUri();
        
        // 1. Validate Method
        if (!"GET".equals(method)) {
            sendError(out, 501, "Not Implemented");
            return;
        }

        // 2. Default to index.html
        if (uri.equals("/")) {
            uri = "/index.html";
        }

        // 3. Extract Host for Virtual Hosting
        String host = request.getHeader("Host");
        if (host == null) host = "default";
        if (host.contains(":")) host = host.split(":")[0];

        // 4. THE FIX: Single, clean log entry with the IP Address
        System.out.println("[SERVICE] IP: " + request.getClientIp() + " | Domain: " + host + " | File: " + uri);

        // 5. Locate and Serve the File
        File file = new File("webroot/" + host + uri);
        
        if (file.exists() && !file.isDirectory()) {
            sendFile(file, out);
        } else {
            sendError(out, 404, "Domain or File Not Found");
        }
    }

    private static void sendFile(File file, OutputStream out) throws IOException {
        String headers = "HTTP/1.1 200 OK\r\n" +
                         "Content-Length: " + file.length() + "\r\n" +
                         "Connection: close\r\n\r\n";
        out.write(headers.getBytes());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        out.flush();
    }

    private static void sendError(OutputStream out, int code, String msg) throws IOException {
        String html = "<html><body><h1>" + code + " " + msg + "</h1></body></html>";
        String response = "HTTP/1.1 " + code + " " + msg + "\r\n" +
                          "Content-Length: " + html.length() + "\r\n" +
                          "Content-Type: text/html\r\n" +
                          "Connection: close\r\n\r\n" + html;
        out.write(response.getBytes());
        out.flush();
    }
}