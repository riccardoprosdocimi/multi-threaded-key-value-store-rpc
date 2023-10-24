package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface server contains methods that all types of translation servers should support.
 */
public interface IServer extends Remote {
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
   * Returns the size of the key-value store.
   *
   * @return the size of the key-value store
   * @exception RemoteException the RMI failure
   */
  int getMapSize() throws RemoteException;

  /**
   * Stops the server.
   *
   * @exception RemoteException the RMI failure
   */
  void shutdown() throws RemoteException;
}
