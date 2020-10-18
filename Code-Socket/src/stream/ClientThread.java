package stream;

import java.io.*;
import java.net.*;

/**
 * Thread côté serveur écoutant les messages venant des clients
 * et notifiant le "controlleur" du serveur de ceux ci
 */
public class ClientThread extends Thread {
  private BufferedReader socIn = null;
  private PrintStream socOut = null;
  private Handler handler;
  private ClientConnectionListener clientConnectionListener;

  /**
   * Constructeur
   * @param clientSocket socket de connection avec le client affilié
   * @param handler interface gérant les messages arrivant du client
   * @param clientConnectionListener interface gérant la connexion (avec un nom) et déconnexion du client
   * @throws IOException erreur provenant de la socket
   */
  public ClientThread(
    Socket clientSocket,
    Handler handler,
    ClientConnectionListener clientConnectionListener
  )
    throws IOException {
    this.handler = handler;
    this.clientConnectionListener = clientConnectionListener;
    this.socIn =
      new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.socOut = new PrintStream(clientSocket.getOutputStream());
  }

  /**
   * Recoit les messages du client et les interprète
   **/
  public void run() {
    try {
      //Read the client name
      final String clientName = socIn.readLine();
      synchronized (clientConnectionListener) {
        clientConnectionListener.onConnect(socOut, clientName);
      }
      handler.handle(clientName + " join the chat !");

      while (true) {
        String line = socIn.readLine();
        if (line == null) {
          synchronized (clientConnectionListener) {
            clientConnectionListener.onDisconnect(socOut, clientName);
          }
          break; // The client disconnected
        }
        synchronized (handler) {
          handler.handle(clientName + " : " + line);
        }
      }

      socIn.close();
      socOut.close();
    } catch (Exception e) {}
  }
}
