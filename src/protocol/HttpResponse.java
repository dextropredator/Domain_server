package protocol;

import utils.Logger;
import utils.MimeTypes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for building and sending HTTP responses.
 */
public class HttpResponse {
    private static final String CRLF = "\r\n";

    public static void sendFile(OutputStream out, Path filePath) {
        // Use a wrapper but DON'T use try-with-resources here on 'out' 
        // unless you want the socket to close immediately every time.
        BufferedOutputStream bout = new BufferedOutputStream(out);
        try {
            String contentType = MimeTypes.getMimeType(filePath);
            long contentLength = Files.size(filePath);

            // Write status and headers
            StringBuilder headers = new StringBuilder();
            headers.append("HTTP/1.1 200 OK").append(CRLF);
            headers.append("Content-Type: ").append(contentType).append(CRLF);
            headers.append("Content-Length: ").append(contentLength).append(CRLF);
            headers.append("Connection: close").append(CRLF);
            headers.append(CRLF);
            
            bout.write(headers.toString().getBytes(StandardCharsets.UTF_8));
            bout.flush();

            // Stream file bytes
            try (InputStream fis = Files.newInputStream(filePath);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] buffer = new byte[8192];
                int read;
                long totalSent = 0;
                while ((read = bis.read(buffer)) != -1) {
                    bout.write(buffer, 0, read);
                    totalSent += read;
                }
                bout.flush();
                // Match the method name from our Logger class
                Logger.addBytesSent(totalSent); 
            }
        } catch (IOException e) {
            Logger.error("Error sending file response: " + e.getMessage());
        }
    }

    public static void sendNotFound(OutputStream out) {
        sendSimpleResponse(out, "404 Not Found", "The requested resource was not found on this server.");
    }

    public static void sendInternalServerError(OutputStream out) {
        sendSimpleResponse(out, "500 Internal Server Error", "An internal server error occurred.");
    }

    public static void sendBadRequest(OutputStream out) {
        sendSimpleResponse(out, "400 Bad Request", "The request could not be understood by the server.");
    }

    public static void sendMethodNotAllowed(OutputStream out) {
        sendSimpleResponse(out, "405 Method Not Allowed", "Only GET method is supported by this server.");
    }

    private static void sendSimpleResponse(OutputStream out, String status, String bodyText) {
        BufferedOutputStream bout = new BufferedOutputStream(out);
        try {
            byte[] body = bodyText.getBytes(StandardCharsets.UTF_8);
            StringBuilder headers = new StringBuilder();
            headers.append("HTTP/1.1 ").append(status).append(CRLF);
            headers.append("Content-Type: text/plain; charset=utf-8").append(CRLF);
            headers.append("Content-Length: ").append(body.length).append(CRLF);
            headers.append("Connection: close").append(CRLF);
            headers.append(CRLF);
            
            bout.write(headers.toString().getBytes(StandardCharsets.UTF_8));
            bout.write(body);
            bout.flush();
            Logger.addBytesSent(body.length);
        } catch (IOException e) {
            Logger.error("Error sending simple response: " + e.getMessage());
        }
    }
}