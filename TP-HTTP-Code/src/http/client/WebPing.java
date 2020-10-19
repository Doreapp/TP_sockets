package http.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {

  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println(
        "Usage java WebPing <server host name> <server port number>"
      );
      return;
    }

    BufferedReader socIn = null;
    PrintStream socOut = null;

    String httpServerHost = args[0];
    int httpServerPort = Integer.parseInt(args[1]);
    httpServerHost = args[0];
    httpServerPort = Integer.parseInt(args[1]);

    try {
      InetAddress addr;
      Socket sock = new Socket(httpServerHost, httpServerPort);
      socIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      socOut = new PrintStream(sock.getOutputStream());

      addr = sock.getInetAddress();
      System.out.println("Connected to " + addr);

      URL url = new URL("http://webserver.test");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");

      String line;
      while (true) {
        line = socIn.readLine();

        if (line == null) break;

        System.out.println("REceive : " + line);
      }

      socOut.close();
      socIn.close();
      sock.close();
    } catch (IOException e) {
      System.out.println(
        "Can't connect to " + httpServerHost + ":" + httpServerPort
      );
      System.out.println(e);
    }
  }
}
