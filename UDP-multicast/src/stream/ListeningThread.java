package stream;

import java.io.*;
import java.net.*;

/*
Classe d'écoute sur la multicastSocket
*/

public class ListeningThread extends Thread {
  private MulticastSocket socket;
  private Handler handler;
  private boolean exit = false;

  public ListeningThread(MulticastSocket socket, Handler handler) {
    this.socket = socket;
    this.handler = handler;
  }

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
        synchronized (handler) {
          if (message.charAt(0) == ';') {
            handler.onConnect(message.substring(1));
          } else if (message.charAt(0) == '.') {
            handler.onDeconnect(message.substring(1));
          } else {
            handler.onMessage(message);
          }
        }
      }
    } catch (IOException exc) {
      if (exc instanceof SocketException) {
        System.out.println("Connection terminée.");
      } else {
        System.out.println("catched exception in ListeningThread : " + exc);
      }
    }
  }

  public void close() {
    exit = true;
  }
}
