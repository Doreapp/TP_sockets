package stream;

import java.io.*;
import java.net.*;

/**
 * Classe envoyant les messages sur le réseau
 **/
public class ClientSendingThread{
  private BufferedReader stdIn = null;
  private PrintStream socOut = null;
  private ConnectionFinishListener listener;
  private String name = null;

  /**
   * Constructeur
   * @param echoSocket socket de connection au réseau
   * @param listener interface écoutant les entrée sur la console
   * @param name surnom utiliser pour le chat
   * @throws IOException si il y a une erreur avec la socket
   */
  public ClientSendingThread(
    Socket echoSocket,
    ConnectionFinishListener listener,
    String name
  )
    throws IOException {
    socOut = new PrintStream(echoSocket.getOutputStream());
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    this.listener = listener;
    this.name = name;
    socOut.println(name);
  }

  /**
   * Envoi un message sur le réseau
   * @param message message à envoyer
   */
  public void sendMessage(String message){
    try{
      if(message != null && message.equals(".")){
        socOut.close();
        stdIn.close();
        if (listener != null) {
          synchronized (listener) {
            listener.onConnectionFinish();
          }
        }
        System.exit(0);
      }
      socOut.println(message);
    }catch(IOException e){}
  }
}
