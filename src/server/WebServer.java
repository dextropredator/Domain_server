package server;

import protocol.HttpRequest;
import utils.FileLoader;
import utils.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer: Listens on configured address and port, accepts connections and dispatches to client handlers.
 */
public class WebServer {
    private final String bindAddress;
    private final int port;
    private final String wwwRoot;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private volatile boolean running = false;

    public WebServer(String bindAddress, int port, String wwwRoot) {
        this.bindAddress = bindAddress;
        this.port = port;
        this.wwwRoot = wwwRoot;
        int threads = Math.max(4, Runtime.getRuntime().availableProcessors() * 2);
        this.threadPool = Executors.newFixedThreadPool(threads);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(bindAddress, port));
            running = true;

            String localIp = findLocalNonLoopbackIPv4();
            Logger.info(String.format("Server started and listening on %s:%d", bindAddress, port));
            if (localIp != null) {
                Logger.info(String.format("Accessible on LAN: http://%s:%d", localIp, port));
            } else {
                Logger.info(String.format("Accessible on: http://%s:%d", bindAddress, port));
            }
            Logger.info("Serving files from: " + FileLoader.getAbsoluteRootPath(wwwRoot));

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(3000); // protect from slowloris
                    threadPool.submit(new ClientHandler(clientSocket, wwwRoot));
                } catch (SocketException se) {
                    if (running) {
                        Logger.error("Socket exception while accepting connections: " + se.getMessage());
                    }
                } catch (IOException e) {
                    Logger.error("I/O error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Logger.error("Failed to start server: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Logger.error("Error closing server socket: " + e.getMessage());
        }
        threadPool.shutdownNow();
    }

    
    private String findLocalNonLoopbackIPv4() {
        try {
            for (NetworkInterface iface : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!iface.isUp() || iface.isLoopback() || iface.isVirtual()) continue;
                for (InetAddress addr : java.util.Collections.list(iface.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Logger.error("Error enumerating network interfaces: " + e.getMessage());
        }
        return null;
    }
}