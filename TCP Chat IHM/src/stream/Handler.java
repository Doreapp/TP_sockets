package stream;

/**
 * Interface gérant les messages reçus par les clients
 */
public interface Handler {
	/**
     * Callback appelé à la reception d'un message
     * @param message message reçu
     */
    void handle(String message);
    
} 