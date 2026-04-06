package server;

import network.HttpConnectionWorkerThread;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {
    private int port;
    private ServerSocket serverSocket;

    public ServerListenerThread(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket);
                workerThread.start();
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Socket Error: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try { serverSocket.close(); } catch (IOException ignored) {}
            }
        }
    }
}