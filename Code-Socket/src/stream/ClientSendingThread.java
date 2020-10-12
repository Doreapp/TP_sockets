/***
 * ClientSendingThread
 * Thread sending data 
 * Date: 12/10/20
 * Authors: Antoine Mandin
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientSendingThread extends Thread {
  private BufferedReader stdIn = null;
  private PrintStream socOut = null;
  private ConnectionFinishListener listener;

  public ClientSendingThread(Socket echoSocket, ConnectionFinishListener listener) throws IOException {
    socOut = new PrintStream(echoSocket.getOutputStream());
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    this.listener = listener;
  }

  @Override
  public void run() {
    try {
      String line;
      while (true) {
        line = stdIn.readLine();
        if (line.equals(".")) break;
        socOut.println(line);
      }
      socOut.close();
      stdIn.close();
    } catch (IOException exc) {
      exc.printStackTrace();
    }
    if(listener != null){
      listener.onConnectionFinish();
    }
  }
}
