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

public class EchoClient implements Handler, ConnectionFinishListener {
  private Socket echoSocket = null;
  private ClientSendingThread sendingThread = null;
  private ClientListeningThread listeningThread = null;
  private JTextPane chat = null;

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

  public void handle(String message) {
    addChat("> "+message);
  }

  private void addChat(String message){
      String fil = chat.getText();
      fil += '\n'+message;
      chat.setText(fil);
   }

   public void sendMessage(String message){
      sendingThread.sendMessage(message);
   }
}
