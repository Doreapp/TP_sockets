package stream;

/**
 * Interface gérant les messages à envoyer, la connexion et la deconnexion
 **/
public interface Sender {
  /**
   * Envoi un messag
   * @param message message à envoyer
   **/
  void send(String message);

  /**
   * Appelée lors de la deconnexion, pour partager l'information sur le réseau
   **/
  void disconnect();

  /**
   * Appelée lors de la connexion pour indiquer le nom au client (controleur)
   * @param name nom du client
   **/
  void connect(String name);
}
