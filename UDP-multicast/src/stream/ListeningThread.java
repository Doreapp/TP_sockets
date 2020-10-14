package stream;

import java.io.*;
import java.net.*;

/*
Classe d'écoute sur la multicastSocket
*/

public class ListeningThread extends Thread 
  implements Handler{
 
  MulticastSocket socket;
  Handler handler;

  public ListeningThread(MulticastSocket socket, Handler handler){
    this.socket = socket;
    this.handler = handler;
  }

  @Override
  publid void run(){
    try{
      for(;;){
          byte[] buf = new byte[1000];
          DatagramPacket recv = new DatagramPacket(buf, buf.length);
          // Receive a datagram packet response
          socket.receive(recv);
          String message = new String(recv);
          message = message.trim();
          // Analyse du message
          if(message[0] == ';'){
            handler.onConnect(message.substring(1));
          }else if(message[0] == '.'){
            handler.onDeconnect(message.substring(1));
          }else{
            handler.handle(message);
          }
      }
    } catch (IOException exc) {
      System.out.println("catched exception in ListeningThread : " + exc);
    }
  }

}
