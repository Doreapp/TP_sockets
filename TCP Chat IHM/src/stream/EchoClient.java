/***
 * EchoClient
 * Example of a TCP client
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;
import javax.swing.JTextPane;

/**
 * Classe du client, gérant la connexion et les messages reçus
 */
public class EchoClient implements Handler, ConnectionFinishListener {
  private Socket echoSocket = null;
  private ClientSendingThread sendingThread = null;
  private ClientListeningThread listeningThread = null;
  private JTextPane chat = null;

  /**
   * Constructeur
   * @param host hôte de la connexion (ex: localhost)
   * @param port port de la connexion
   * @param name surnom retenu pour le chat
   * @param chat contenant du chat du client
   * @throws IOException
   */
  public EchoClient(String host, String port, String name, JTextPane chat) throws IOException {
    try {
      this.chat = chat;
      // creation socket ==> connexion
      echoSocket = new Socket(host, new Integer(port).intValue());

      sendingThread = new ClientSendingThread(echoSocket, this, name);
      listeningThread = new ClientListeningThread(echoSocket, this);

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
   * Affiche le message dans le chat
   */
  public void handle(String message) {
    // addChat("> "+message);
    String fil = chat.getText();
    fil += '\n'+message;
    chat.setText(fil);
  }

  private void addChat(String message){
      String fil = chat.getText();
      fil += '\n'+message;
      chat.setText(fil);
   }

   /**
   * Demande l'envoi du message transmit par l'IHM
   */
   public void sendMessage(String message){
      sendingThread.sendMessage(message);
   }
}
