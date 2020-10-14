package stream;

import java.io.*;
import java.net.*;

public class Client implements Sender, Handler {
  private String name = "unnamed";
  private MulticastSocket groupSocket = null;
  private InetAddress groupAddr;
  private int groupPort;
  private SendingThread sendingThread;
  private ListeningThread listeningThread;

  public static void main(String[] args) {
    Client client = new Client("224.125.85.13", 1234);
  }

  public Client(String groupAddressStr, int groupPort) {
    try {
      groupAddr = InetAddress.getByName(groupAddressStr);
      this.groupPort = groupPort;

      // Create a multicast socket
      groupSocket = new MulticastSocket(groupPort);
      // Join the group
      groupSocket.joinGroup(groupAddr);

      sendingThread = new SendingThread(this);
      listeningThread = new ListeningThread(groupSocket, this);

      sendingThread.start();
      listeningThread.start();
    } catch (Exception e) {
      System.out.println("Error in Client : " + e);
    }
  }

  @Override
  public void send(String message) {
    log("send (" + message + ")");
    if (groupSocket == null) {
      System.out.println("Error in Client : send() with null groupSocket");
      return;
    }

    String formatedMessage = name + " : " + message;

    sendMessage(formatedMessage);
  }

  @Override
  public void disconnect() {
    log("disconnect ");
    if (groupSocket == null) {
      System.out.println(
        "Error in Client : disconnect() with null groupSocket"
      );
      return;
    }

    String formatedMessage = "." + name;

    sendMessage(formatedMessage);

    close();
  }

  @Override
  public void connect(String name) {
    log("connect(" + name + ")");
    this.name = name;

    String formatedMessage = ";" + name;

    sendMessage(formatedMessage);
  }

  @Override
  public void onMessage(String message) {
    showMessage(message);
  }

  @Override
  public void onConnect(String name) {
    showMessage(name + " joined the chat.");
  }

  @Override
  public void onDeconnect(String name) {
    showMessage(name + " left the chat.");
  }

  private void log(String msg) {
    //System.out.println("[LOG] Client : " + msg);
  }

  private void showMessage(String message) {
    // Pour l'instant affiche sur la console ...
    System.out.println("> " + message);
  }

  private void sendMessage(String msg) {
    log("send message : " + msg);
    if (groupSocket == null) {
      System.out.println(
        "Error in Client : sendMessage() with null groupSocket"
      );
      return;
    }

    DatagramPacket packet = new DatagramPacket(
      msg.getBytes(),
      msg.length(),
      groupAddr,
      groupPort
    );

    try {
      groupSocket.send(packet);
    } catch (IOException e) {
      System.out.println("Error in Client, Socket.send : " + e);
    }
  }

  private void close(){
    listeningThread.close();
    groupSocket.close();
  }
}
