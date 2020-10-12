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

  ClientThread(Socket s) {
    this.clientSocket = s;
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
      System.out.println(clientName+" join the chat !");
      socOut.println(clientName);
      while (true) {
        String line = socIn.readLine();
        if (line == null) break; // The client disconnected
        System.out.println(clientName + " : " + line);
        socOut.println(line);
      }
    } catch (Exception e) {
      System.err.println("Error in EchoServer:" + e);
    }
    System.out.println(
      "Log (" + clientSocket.getInetAddress() + ") : Client disconnected."
    );
  }
}
