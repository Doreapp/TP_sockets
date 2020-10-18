package stream;

import java.io.*;
import java.net.*;

/**
 * Classe du client, gérant la connexion et les messages reçus
 */
public class EchoClient implements Handler, ConnectionFinishListener {
  private Socket echoSocket = null;
  private ClientSendingThread sendingThread = null;
  private ClientListeningThread listeningThread = null;

  /**
   *  Méthode principale
   * @param args options d'entrée console
   **/
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println(
        "Usage: java EchoClient <EchoServer host> <EchoServer port>"
      );
      System.exit(1);
    }
    EchoClient client = new EchoClient(args[0], args[1]);
  }

  /**
   * Constructeur
   * @param host hôte de la connexion (ex: localhost)
   * @param port port de la connexion
   */
  public EchoClient(String host, String port){
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

  /**
   * callback appelé lorsque la connexion se termine
   * Arrête le thread d'écoute des messages venant du serveret ferme la socket
   */
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

  /**
   * Callback appelé à la reception d'un message
   * Affiche le message sur la console
   */
  public void handle(String message) {
    System.out.println("> " + message);
  }
}
