package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import utils.ILogger;
import utils.Logger;

public class Server extends UnicastRemoteObject implements IServer {
  private Registry registry;
  private final Map<String, String> dictionary;
  private final ILogger logger;

  public Server() throws RemoteException {
    super();
    Map<String, String> hashMap = new HashMap<>();
    this.dictionary = Collections.synchronizedMap(hashMap); // make the hashmap thread-safe
    this.logger = new Logger("ServerLogger", "ServerLog.log"); // already thread-safe
    this.execute();
  }

  private void execute() throws RemoteException {
    try {
      this.registry = LocateRegistry.createRegistry(50000);
      registry.rebind("//localhost/TranslationServer", this);
      this.logger.log("TranslationServer bound in registry");
      System.out.println("TranslationServer bound in registry");
      this.logger.log("TranslationServer is running...");
      System.out.println("TranslationServer is running...");
    } catch (Exception e) {
      this.logger.log("TranslationServer error: " + e.getMessage());
      System.err.println("TranslationServer error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key   the word to be translated
   * @param value the translation
   * @return the outcome of the operation
   */
  @Override
  public String put(String key, String value) {
    if (this.dictionary.containsKey(key)) {
      this.logger.log("FAIL: the translation for \"" + key + "\" already exists");
      return "FAIL: the translation for \"" + key + "\" already exists";
    } else {
      this.dictionary.put(key.toLowerCase(), value.toLowerCase());
      this.logger.log("SUCCESS: added the key \"" + key + "\" associated with \"" + value + "\"");
      return "SUCCESS";
    }
  }

  /**
   * Retrieves the value of a key.
   *
   * @param key the word to be translated
   * @return the translation
   */
  @Override
  public String get(String key) {
    key = key.toLowerCase();
    String translation;
    if (this.dictionary.containsKey(key)) {
      translation = this.dictionary.get(key);
      this.logger.log("SUCCESS: returned the value \"" + translation + "\" associated with \"" + key + "\"");
    } else {
      translation = "FAIL: I don't know the translation for " + "\"" + key + "\"" + " yet";
      this.logger.log("FAIL: no value is associated with \"" + key + "\"");
    }
    return translation;
  }

  /**
   * Removes a key-value pair.
   *
   * @param key the word to be deleted
   * @return the outcome of the operation
   */
  @Override
  public String delete(String key) {
    key = key.toLowerCase();
    if (this.dictionary.containsKey(key)) {
      this.dictionary.remove(key);
      this.logger.log("SUCCESS: deleted the key-value pair associated with \"" + key + "\"");
      return "SUCCESS";
    } else {
      this.logger.log("FAIL: " + "\"" + key + "\" does not exist");
      return "FAIL: " + "\"" + key + "\" does not exist";
    }
  }

  /**
   * Returns the size of the key-value store.
   *
   * @return the size of the key-value store
   * @exception RemoteException the RMI failure
   */
  @Override
  public int getMapSize() throws RemoteException {
    return this.dictionary.size();
  }

  /**
   * Stops the server.
   *
   * @throws RemoteException the RMI failure
   */
  @Override
  public void shutdown() throws RemoteException {
    this.logger.log("Received a request to shut down...");
    System.out.println("TranslationServer is shutting down...");
    try {
      this.registry.unbind("//localhost/TranslationServer");
      this.logger.log("TranslationServer unbound in registry");
    } catch (NotBoundException e) {
      this.logger.log("Unbind error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    UnicastRemoteObject.unexportObject(this, true);
    this.logger.log("TranslationServer unexported");
    this.logger.log("TranslationServer closed");
    this.logger.close();
    System.out.println("TranslationServer closed");
  }
}
