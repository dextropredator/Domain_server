package network;

import protocol.HttpRequestParser;
import model.HttpRequest;
import service.VirtualHostService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            HttpRequest request = HttpRequestParser.parse(in);

            if (request != null) {
                
                String ipAddress = socket.getInetAddress().getHostAddress();
                request.setClientIp(ipAddress);

                System.out.println("\n[NETWORK] Received: " + request.getMethod() + " " + request.getUri() + " from " + ipAddress);
                VirtualHostService.serve(request, out);
            }

        } catch (IOException e) {
            System.err.println("[NETWORK] Connection Error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}