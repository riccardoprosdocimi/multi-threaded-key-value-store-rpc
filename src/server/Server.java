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

/**
 * The type Server represents a translation server that communicates via Remote Method Invocation (RMI).
 */
public class Server extends UnicastRemoteObject implements IServer {
  private Registry registry;
  private final Map<String, String> dictionary;
  private final ILogger logger;

  /**
   * Instantiates a new Server.
   *
   * @param port the port number
   * @throws RemoteException the RMI failure
   */
  public Server(int port) throws RemoteException {
    super(); // export the remote object
    Map<String, String> store = new HashMap<>(); // instantiate the key-value store
    this.dictionary = Collections.synchronizedMap(store); // make the key-value store thread-safe
    this.logger = new Logger("ServerLogger", "ServerLog.log"); // instantiate a logging system that already is thread-safe
    if (port >= 49152 && port <= 65535 || port == 1099) { // limit port numbers to valid values
      this.execute(port);
    } else {
      this.logger.log("Invalid port number entered by the user: " + port);
      System.err.println("Invalid port number. Please enter a port within the range 49152-65535 or 1099");
      this.logger.close();
      System.exit(1);
    }
  }

  private void execute(int port) throws RemoteException {
    try {
      this.registry = LocateRegistry.createRegistry(port); // create a registry at the user input port
      registry.rebind("TranslationServer", this); // bind the remote object to a custom name
      this.logger.log("TranslationServer bound in registry");
      System.out.println("TranslationServer bound in registry");
      this.logger.log("TranslationServer is running on port " + port + "...");
      System.out.println("TranslationServer is running on port " + port + "...");
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
   * @throws RemoteException the RMI failure
   */
  @Override
  public String put(String key, String value) throws RemoteException {
    key = key.toLowerCase();
    if (this.dictionary.containsKey(key)) { // if the key is already in the store
      this.logger.log("FAIL: the translation for \"" + key + "\" already exists");
      return "FAIL: the translation for \"" + key + "\" already exists";
    } else {
      value = value.toLowerCase();
      this.dictionary.put(key, value);
      this.logger.log("SUCCESS: added the key \"" + key + "\" associated with \"" + value + "\"");
      return "SUCCESS";
    }
  }

  /**
   * Retrieves the value of a key.
   *
   * @param key the word to be translated
   * @return the translation
   * @throws RemoteException the RMI failure
   */
  @Override
  public String get(String key) throws RemoteException {
    key = key.toLowerCase();
    String translation;
    if (this.dictionary.containsKey(key)) { // if the key exists
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
   * @throws RemoteException the RMI failure
   */
  @Override
  public String delete(String key) throws RemoteException {
    key = key.toLowerCase();
    if (this.dictionary.containsKey(key)) { // if the key exists
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
   * @throws RemoteException the RMI failure
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
      this.registry.unbind("TranslationServer"); // unbind the remote object from the custom name
      this.logger.log("TranslationServer unbound in registry");
    } catch (NotBoundException e) {
      this.logger.log("Unbind error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    UnicastRemoteObject.unexportObject(this, true); // unexport the remote object
    this.logger.log("TranslationServer unexported");
    this.logger.log("TranslationServer closed");
    this.logger.close();
    System.out.println("TranslationServer closed");
  }
}
