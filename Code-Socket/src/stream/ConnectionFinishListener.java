package stream;

/**
 * Interface gérant la deconnexion au serveur
 */
public interface ConnectionFinishListener {
  /**
   * Callback appelé lorsque la connexion se termine
   */
  void onConnectionFinish();
}
