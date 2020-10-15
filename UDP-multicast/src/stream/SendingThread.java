package stream;

import java.io.*;
import java.net.*;

/**
 * Thread utilisé pour lire et interpréter les entrées sur la console, puis
 * les passer au client
 **/
public class SendingThread extends Thread {
  // Reader de la console
  private BufferedReader stdIn = null;

  // Interface gérant les connections, deconnections messages à envoyer
  private Sender sender;

  private boolean exit = false;

  /**
   * Constructeur
   * @param sender interface gérant les connections, deconnections messages à envoyer
   **/
  public SendingThread(Sender sender) {
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    this.sender = sender;
  }

  /**
   * Méthode principale du thread
   * Ecoute les entrées sur la console
   * puis les traite (. = se déconnecter par exemple)
   **/
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

  /**
  * Fait en sorte que le Thread se termine "proprement"
  * = arrête la boucle while
  */
  public void close() {
    exit = true;
  }
}
