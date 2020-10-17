package stream;

import java.io.*;
import java.net.*;

public class ClientSendingThread{
  private BufferedReader stdIn = null;
  private PrintStream socOut = null;
  private ConnectionFinishListener listener;
  private String name = null;

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
    }catch(IOException e){

    }
  }
}
