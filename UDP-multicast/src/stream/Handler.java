package stream;

/**
 * Interface gérant les messages reçus
 **/
public class Handler {

  /**
   * Appelé lors de la reception d'un message
   * @param message message "classique" reçu
   **/
  void onMessage(String message);

  /**
   * Appelé lors de la reception d'une connexion
   * @param name nom du client qui se connecte
   **/
  void onConnect(Stirng name);

  /**
   * Appelé lors de la reception d'une déconnexion
   * @param name nom du client qui se déconnecte
   **/
  void onDeconnect(String name);
}
