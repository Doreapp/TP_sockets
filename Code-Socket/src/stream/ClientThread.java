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
  private BufferedReader socIn = null;
  private PrintStream socOut = null;
  private Handler handler;
  private ClientConnectionListener clientConnectionListener;

  public ClientThread(
    Socket clientSocket,
    Handler handler,
    ClientConnectionListener clientConnectionListener
  ) throws IOException {
    this.handler = handler;
    this.clientConnectionListener = clientConnectionListener;
    this.socIn =
      new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.socOut = new PrintStream(clientSocket.getOutputStream());
  }

  /**
   * receives a request from client then sends an echo to the client
   *  
   **/
  public void run() {
    try {
      //Read the client name
      final String clientName = socIn.readLine();
      clientConnectionListener.onConnect(socOut, clientName);
      handler.handle(clientName + " join the chat !");

      while (true) {
        String line = socIn.readLine();
        if (line == null) {
          clientConnectionListener.onDisconnect(socOut, clientName);
          break; // The client disconnected
        }
        handler.handle(clientName + " : " + line);
      }

      socIn.close();
      socOut.close();
    } catch (Exception e) {
    }
  }
}
