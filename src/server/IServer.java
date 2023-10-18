package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.ILogger;

/**
 * The interface server contains methods that all types of translation servers should support.
 */
public interface IServer extends Remote {
  /**
   * Sets the port number.
   *
   * @param port the port
   */
  //void setPortNumber(String port);

  /**
   * Starts the server.
   *
   * @exception RemoteException the RMI failure
   */
  void execute() throws RemoteException;

  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key   the word to be translated
   * @param value the translation
   * @return the outcome of the operation
   * @exception RemoteException the RMI failure
   */
  String put(String key, String value) throws  RemoteException;

  /**
   * Retrieves the value of a key.
   *
   * @param key the word to be translated
   * @return the translation
   * @exception RemoteException the RMI failure
   */
  String get(String key) throws RemoteException;

  /**
   * Removes a key-value pair.
   *
   * @param key the word to be deleted
   * @return the outcome of the operation
   * @exception RemoteException the RMI failure
   */
  String delete(String key) throws RemoteException;

  /**
   * Stops the server.
   *
   * @exception RemoteException the RMI failure
   */
  void shutdown() throws RemoteException;
}
