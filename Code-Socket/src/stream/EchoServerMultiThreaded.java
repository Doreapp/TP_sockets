/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class EchoServerMultiThreaded
  implements Handler, ClientConnectionListener {
  private ServerSocket listenSocket;
  private List<PrintStream> clientOuts = new ArrayList<PrintStream>();
  private List<String> clientNames = new ArrayList<String>();

  /**
   * main method
   *
   * @param EchoServer
   *            port
   *
   **/
  public static void main(String args[]) throws IOException {
    if (args.length != 1) {
      System.out.println("Usage: java EchoServer <EchoServer port>");
      System.exit(1);
    }
    EchoServerMultiThreaded server = new EchoServerMultiThreaded(args[0]);
  }

  public EchoServerMultiThreaded(String port) throws IOException {
    try {
      listenSocket = new ServerSocket(Integer.parseInt(port)); // port
      System.out.println("Server ready...");
      while (true) {
        Socket clientSocket = listenSocket.accept();
        System.out.println("Connexion from:" + clientSocket.getInetAddress());
        ClientThread ct = new ClientThread(clientSocket, this, this);
        ct.start();
      }
    } catch (Exception e) {
      System.err.println("Error in EchoServerMultiThreaded:" + e);
      e.printStackTrace();
    }
  }

  public void handle(String message) {
    for (int i = 0; i < clientOuts.size(); i++) {
      clientOuts.get(i).println(message);
    }
  }

  @Override
  public void onDisconnect(PrintStream socOut, String name) {
    for (int i = 0; i < clientOuts.size(); i++) {
      if (clientOuts.get(i) == socOut) {
        clientOuts.remove(i);
        break;
      }
    }
    clientNames.remove(name);
    handle(name + " has left !");
  }

  @Override
  public void onConnect(PrintStream socOut, String name) {
    String names = "[Console] : ";
    int clientCount = clientNames.size();
    if (clientCount > 1) {
      for (int i = 0; i < clientCount - 1; i++) {
        names += clientNames.get(i) + ", ";
      }
    }
    if (clientCount > 0) {
      names += clientNames.get(clientCount - 1) + " ";
      names += clientCount > 1 ? "are" : "is";
      names += " in the chat.";
    } else {
      names += "Welcome in the chat. You're the first one !";
    }
    socOut.println(names);

    clientNames.add(name);
    clientOuts.add(socOut);
  }
}
