package server;

import protocol.HttpRequest;
import protocol.HttpResponse;
import utils.FileLoader;
import utils.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.time.Instant;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final String wwwRoot;

    public ClientHandler(Socket clientSocket, String wwwRoot) {
        this.clientSocket = clientSocket;
        this.wwwRoot = wwwRoot;
    }

    @Override
    public void run() {
        String clientIp = clientSocket.getInetAddress().getHostAddress();
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {
            
            HttpRequest request = HttpRequest.parse(in);
            if (request == null) {
                Logger.logRequest(clientIp, "???", "INVALID", Instant.now(), null);
                HttpResponse.sendBadRequest(out);
                return;
            }

            Logger.logRequest(clientIp, request.getPath(), request.getMethod(), Instant.now(), request.getHeader("User-Agent"));

            if (!"GET".equalsIgnoreCase(request.getMethod())) {
                HttpResponse.sendMethodNotAllowed(out);
                return;
            }

            String path = request.getPath();
            
            // --- FIX: Strip absolute URLs sent by the Proxy ---
            if (path != null && path.startsWith("http")) {
                int start = path.indexOf("://");
                if (start != -1) {
                    int slashIndex = path.indexOf('/', start + 3);
                    if (slashIndex != -1) {
                        path = path.substring(slashIndex);
                    } else {
                        path = "/";
                    }
                }
            }
            
            String host = request.getHeader("Host");
            
            if (host != null) {
                if (host.contains(":")) {
                    host = host.substring(0, host.indexOf(':'));
                }
                
                if (path == null || path.equals("") || path.equals("/")) {
                    path = "/" + host + "/index.html";
                } else {
                    path = "/" + host + path;
                }
            } else {
                if (path == null || path.equals("") || path.equals("/")) {
                    path = "/index.html";
                }
            }

            Path filePath = FileLoader.resolveFile(wwwRoot, path);
            if (filePath == null) {
                Path notFound = FileLoader.getCustom404(wwwRoot);
                if (notFound != null) {
                    HttpResponse.sendFile(out, notFound);
                } else {
                    HttpResponse.sendNotFound(out);
                }
                return;
            }

            HttpResponse.sendFile(out, filePath);

        } catch (Exception e) {
            Logger.error("Exception handling client " + clientIp + ": " + e.getMessage());
            try {
                OutputStream out = clientSocket.getOutputStream();
                HttpResponse.sendInternalServerError(out);
            } catch (IOException ignored) {
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}