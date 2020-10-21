package stream;

import java.io.*;
import java.net.*;

/**
 * Classe d'écoute sur la multicastSocket
 **/
public class ListeningThread extends Thread {
  // Socket liée au groupe
  MulticastSocket socket;

  // Interface gérant les messages / évènnements entrant
  Handler handler;

  /**
   * Constructeur
   * @param socket Socket liée au groupe
   * @param handler interface gérant les messages / évènnements entrant
   **/
  public ListeningThread(MulticastSocket socket, Handler handler) {
    this.socket = socket;
    this.handler = handler;
  }

  /**
   * Méthode principale du Thread
   * écoute les messages entrant puis les traites
   **/
  @Override
  public void run() {
    try {
      for (;;) {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        // Receive a datagram packet response
        socket.receive(recv);
        String message = new String(buf);
        message = message.trim();
        // Analyse du message
        if (message.charAt(0) == ';') {
          handler.onConnect(message.substring(1));
        } else if (message.charAt(0) == '.') {
          handler.onDeconnect(message.substring(1));
        } else {
          handler.onMessage(message);
        }
      }
    } catch (IOException exc) {
      if (exc instanceof SocketException) {
        System.out.println("Connexion finished");
      } else {
        System.out.println("Error on Listening Thread : " + exc);
      }
    }
  }
}
