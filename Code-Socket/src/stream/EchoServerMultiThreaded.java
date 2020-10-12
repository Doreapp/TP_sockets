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

public class EchoServerMultiThreaded implements Handler,ClientDeconnection {
  ServerSocket listenSocket;
  List<Socket> clientListe = new ArrayList<Socket>();

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
        clientListe.add(clientSocket);
        System.out.println("Connexion from:" + clientSocket.getInetAddress());
        ClientThread ct = new ClientThread(clientSocket, this, this);
        ct.start();
      }
    } catch (Exception e) {
      System.err.println("Error in EchoServer:" + e);
    }
  }

  public void handle(String message) {
    try {
      for (int i = 0; i < clientListe.size(); i++) {
        PrintStream socOut = new PrintStream(
          clientListe.get(i).getOutputStream()
        );
        socOut.println(message);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void disconnect(Socket soc, String name) {
	  for (int i = 0; i < clientListe.size(); i++) {
        if(clientListe.get(i) == soc) {
        	clientListe.remove(i);
        	break;
        }
      }
	  handle(name+" has left !");
  }
}
