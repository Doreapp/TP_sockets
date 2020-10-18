package stream;

import java.io.*;
import java.net.*;

/**
 * Classe Thread, côté client, écoutant les messages arrivant du serveur
 **/
public class ClientListeningThread extends Thread {
  // Stream reader des message arrivant du Serveur
  private BufferedReader socIn = null;

  // Interface gérant les messages entrant
  private Handler handler;

  // Booleén pour le thread, indiquant s'il doit stopper son exécution
  private boolean exit = false;

  /**
   * Constructeur de la classe
   * @param echoSocket socket représentant la connection avec le serveur
   * @param handler interface gérant les messages reçus
   * @throws IOException erreurs pouvant venir du socket
   **/
  public ClientListeningThread(Socket echoSocket, Handler handler)
    throws IOException {
    socIn =
      new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
    this.handler = handler;
  }

  /**
   * méthode principale du thread
   * Ecoute les messages venant du serveur
   **/
  @Override
  public void run() {
    try {
      String line;
      while (!exit) {
        line = socIn.readLine();

        if (handler != null) {
          synchronized (handler) {
            handler.handle(line);
          }
        }
      }
      close();
    } catch (IOException e) {}
  }

  /**
   * Arrête le thread et ferme la socket
   * @throws IOException erreur pouvant être provoquer par la fermeture de la socket
   */
  public void close() throws IOException {
    exit = true;
    socIn.close();
  }
}
