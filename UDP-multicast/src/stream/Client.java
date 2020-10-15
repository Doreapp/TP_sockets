package stream;

import java.io.*;
import java.net.*;

/**
 * Classe implémantant un client du chat.
 * Sert de controleur, interface entre les entrées console, les entrées réseaux et
 * les sorties réseaux
 **/
public class Client implements Sender, Handler {
  // Nom / pseudo de l'utilisateur
  private String name = "unnamed";

  // Socket
  private MulticastSocket groupSocket = null;

  // information du groupe
  private InetAddress groupAddr;
  private int groupPort;

  // Threads d'I/O
  private SendingThread sendingThread;
  private ListeningThread listeningThread;

  /**
   * Méthode principale
   * Instancie un objet Client
   * @param args options entrées dans la console
   **/
  public static void main(String[] args) {
    Client client = new Client("224.125.85.13", 1234);
  }

  /**
   * Constructeur
   * @param groupAddressStr adresse du groupe
   * @param groupPort port du groupe
   **/
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

  /**
   * Envoie un message entré par l'utilisateur au groupe
   * @param message texte entré par l'utilisateur
   **/
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

  /**
   * Déconnecte l'utilisateur
   **/
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

  /**
   * Connecte l'utilisateur, en spécifiant son pseudo
   * @param name pseudo de l'utilisateur
   **/
  @Override
  public void connect(String name) {
    log("connect(" + name + ")");
    this.name = name;

    String formatedMessage = ";" + name;

    sendMessage(formatedMessage);
  }

  /**
   * Appelée à la reception d'un message d'un utilisateur,
   * en provenance du groupe
   * @param message texte reçu
   **/
  @Override
  public void onMessage(String message) {
    showMessage(message);
  }

  /**
   * Appelée à la reception d'un nouvelle connection au groupe
   * @param name pseudo de l'utilisateur qui vient de se connecter
   **/
  @Override
  public void onConnect(String name) {
    showMessage(name + " joined the chat.");
  }

  /**
   * Appelée à la déconnection d'un utilisateur du groupe
   * @param name pseudo de l'utilisateur qui s'est déconnecté
   **/
  @Override
  public void onDeconnect(String name) {
    showMessage(name + " left the chat.");
  }

  /**
   * Méthode utilisée pour le débug
   * Si activée : envoi un message type "[LOG] Client : ..." sur la sortie standart
   * @param msg message particulier à envoyer
   **/
  private void log(String msg) {
    //System.out.println("[LOG] Client : " + msg);
  }

  /**
   * Affiche un message quelconque sur la sortie standart (console)
   * @param message texte à afficher
   **/
  private void showMessage(String message) {
    // Pour l'instant affiche sur la console ...
    System.out.println("> " + message);
  }

  /**
   * Envoi un message quelconque au groupe UDP
   * @param msg texte à envoyer
   **/
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

  /**
   * Ferme les threads et connexions
   **/
  private void close() {
    listeningThread.close();
    groupSocket.close();
  }
}
