import java.net.*;
import java.io.*;
public class Https {
   public static void main(String[] args) throws Exception{
    ServerSocket s = new ServerSocket(8080);
Socket c = s.accept();
c.getOutputStream().write("Hello dost\n".getBytes());
c.close();;
s.close();

   }

}