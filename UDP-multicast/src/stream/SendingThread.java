package stream;

import java.io.*;
import java.net.*;

public class SendingThread extends Thread {
  private BufferedReader stdIn = null;
  private Sender sender;
  private boolean exit = false;

  public SendingThread(SEnder sender) {
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    this.sender = sender;
  }

  @Override
  public void run() {
    try {
      String line;

      // name input
      System.out.println("Enter your nickname : ");
      line = stdIn.readLine();
      synchronized (sender) {
        sender.connect(line);
      }

      // messages input
      while (!exit) {
        line = stdIn.readLine();
        if (line.equals(".")) break;
        synchronized (sender) {
          sender.send(line);
        }
      }
      stdIn.close();
    } catch (IOException exc) {
      System.out.println("catched exception in SendingThread : " + exc);
    }
    synchronized (sender) {
      sender.disconnect();
    }
  }

  public void close() {
    exit = true;
  }
}
