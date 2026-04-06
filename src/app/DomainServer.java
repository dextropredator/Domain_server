package app;

import server.ServerListenerThread;
import java.io.IOException;

public class DomainServer {
   public static void main(String[] args){
      int port = 8082;
      System.out.println("[APP] Domain Server starting on Port: " + port);

      try {
         ServerListenerThread listener = new ServerListenerThread(port);
         listener.start();
      }
      catch (IOException e){
         System.err.println("[APP] Failed to start server: " + e.getMessage());
      }
   }
}