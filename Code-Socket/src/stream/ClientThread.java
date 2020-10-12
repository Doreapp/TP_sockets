/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
  private Socket clientSocket;
  Handler handler;

  ClientThread(Socket s, Handler handler) {
    this.clientSocket = s;
    this.handler = handler;
  }

  /**
   * receives a request from client then sends an echo to the client
   * @param clientSocket the client socket
   **/
  public void run() {
    try {
      BufferedReader socIn = null;
      socIn =
        new BufferedReader(
          new InputStreamReader(clientSocket.getInputStream())
        );
      PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
      String clientName = socIn.readLine();
      handler.handle(clientName+" join the chat !");
      while (true) {
        String line = socIn.readLine();
        if (line == null) break; // The client disconnected
        handler.handle(clientName+" : "+line);
      }
    } catch (Exception e) {
      System.err.println("Error in EchoServer:" + e);
    }
    System.out.println(
      "Log (" + clientSocket.getInetAddress() + ") : Client disconnected."
    );
  }
}
