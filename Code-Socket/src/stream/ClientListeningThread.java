/***
 * ClientListeningThread
 * Thread listening for informations and displaying it on console
 * Date: 12/10/20
 * Authors: Antoine Mandin
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientListeningThread extends Thread {
  private BufferedReader socIn = null;
  private Handler handler;
  private boolean exit = false;

  public ClientListeningThread(Socket echoSocket, Handler handler)
    throws IOException {
    socIn =
      new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
    this.handler = handler;
  }

  @Override
  public void run() {
    try {
      String line;
      while (!exit) {
        line = socIn.readLine();

        if (handler != null) {
          handler.handle(line);
        }
      }
      close();
    } catch (IOException e) {}
  }

  public void close() throws IOException {
    exit = true;
    socIn.close();
  }
}
