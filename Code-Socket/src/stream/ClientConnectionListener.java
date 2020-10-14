package stream;

import java.io.*;
import java.net.*;

/**
 * Interface pour le thread Server, indiquant les connexions et deconnexions d'utilisateurs
 * Avec leurs nom et Print Strem out
 **/
public interface ClientConnectionListener {
  /**
   * Méthode indiquant la déconnexion d'un client 
   * @params socOut Stream pour communiquer en sortie (out) au client
   * @params name Nom du client 
   **/
  void onDisconnect(PrintStream socOut, String name);

   /**
   * Méthode indiquant la connexion d'un client 
   * @params socOut Stream pour communiquer en sortie (out) au client
   * @params name Nom du client 
   **/
  void onConnect(PrintStream socOut, String name);
}
