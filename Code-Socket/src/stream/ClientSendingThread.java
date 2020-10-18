package stream;

import java.io.*;
import java.net.*;

/**
 * Classe Thread, côté client, écoutant l'entrée standart
 * et envoyant les messages sur le réseau
 **/
public class ClientSendingThread extends Thread {
  private BufferedReader stdIn = null;
  private PrintStream socOut = null;
  private ConnectionFinishListener listener;

  /**
   * Constructeur
   * @param echoSocket socket de connection au réseau
   * @param listener interface écoutant les entrée sur la console
   * @throws IOException si il y a une erreur avec la socket
   */
  public ClientSendingThread(
    Socket echoSocket,
    ConnectionFinishListener listener
  )
    throws IOException {
    socOut = new PrintStream(echoSocket.getOutputStream());
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    this.listener = listener;
  }

  /**
   * Méthode principale écoutant les entrées sur la console
   * Et envoyant les messages sur le réseau
   */
  @Override
  public void run() {
    try {
      String line;
      System.out.println("Enter your nickname : ");
      while (true) {
        line = stdIn.readLine();
        if (line.equals(".")) break;
        socOut.println(line);
      }
      socOut.close();
      stdIn.close();
    } catch (IOException exc) {}
    if (listener != null) {
      synchronized (listener) {
        listener.onConnectionFinish();
      }
    }
  }
}
