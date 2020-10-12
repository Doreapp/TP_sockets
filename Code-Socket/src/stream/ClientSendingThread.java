/***
 * ClientThread
 * Example of a TCP server
 * Date: 12/10/20
 * Authors: Antoine Mandin
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientSendingThread extends Thread {
  BufferedReader stdIn = null;
  PrintStream socOut = null;
  BufferedReader socIn = null;

  public ClientSendingThread(Socket echoSocket) throws IOException {
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    socOut = new PrintStream(echoSocket.getOutputStream());
    socIn =
      new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
  }

  @Override
  public void run() {
    try {
      String line;
      while (true) {
        line = stdIn.readLine();
        if (line.equals(".")) break;
        socOut.println(line);
        System.out.println("echo: " + socIn.readLine());
      }
      socOut.close();
      stdIn.close();
      socIn.close();
    } catch (IOException exc) {
      exc.printStackTrace();
    }
  }
}
