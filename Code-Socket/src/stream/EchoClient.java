/***
 * EchoClient
 * Example of a TCP client
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;

public class EchoClient implements Handler, ConnectionFinishListener {
  private Socket echoSocket = null;
  private ClientSendingThread sendingThread = null;
  private ClientListeningThread listeningThread = null;

  /**
   *  main method
   *  accepts a connection, receives a message from client then sends an echo to the client
   **/
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.out.println(
        "Usage: java EchoClient <EchoServer host> <EchoServer port>"
      );
      System.exit(1);
    }
    EchoClient client = new EchoClient(args[0], args[1]);
  }

  public EchoClient(String host, String port) throws IOException {
    try {
      // creation socket ==> connexion
      echoSocket = new Socket(host, new Integer(port).intValue());

      sendingThread = new ClientSendingThread(echoSocket, this);
      listeningThread = new ClientListeningThread(echoSocket, this);

      sendingThread.start();
      listeningThread.start();
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host:" + host);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for " + "the connection to:" + host);
      System.exit(1);
    }
  }

  @Override
  public void onConnectionFinish() {
    try {
      listeningThread.close();
      echoSocket.close();
    } catch (IOException e) {
      e.printStackTrace();    
      System.exit(1);
    }
  }

  public void handle(String message) {
    System.out.println("> " + message);
  }
}
