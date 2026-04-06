package app;

import server.WebServer;
import utils.Logger;

public class MainServer {
    public static void main(String[] args) {
        // Root folder for static files
        String wwwRoot = "www";
        int port = 8080;
        boolean enableFileLog = true; // set to false to disable server.log output

        Logger.init(enableFileLog);

        WebServer server = new WebServer("0.0.0.0", port, wwwRoot);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.info("Shutdown requested. Stopping server...");
            server.stop();
            Logger.close();
            Logger.info("Server stopped.");
        }));

        server.start();
    }
}