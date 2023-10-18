package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.ILogger;
import utils.Logger;

public class Server extends UnicastRemoteObject implements IServer {
  private final Map<String, String> dictionary;
  private final ILogger logger;
  private final ExecutorService executor;

  public Server() throws RemoteException {
    super();
    Map<String, String> hashMap = new HashMap<>();
    this.dictionary = Collections.synchronizedMap(hashMap); // make the hashmap thread-safe
    this.logger = new Logger("ServerLogger", "ServerLog.log");
    this.executor = Executors.newCachedThreadPool(); // create a thread pool for handling client requests
  }

  /**
   * Starts the server.
   *
   * @throws RemoteException the RMI failure
   */
  @Override
  public void execute() throws RemoteException {
    try {
      IServer stub = (IServer) UnicastRemoteObject.exportObject(this, 0);
      Registry registry = LocateRegistry.createRegistry(1099);
      registry.rebind("TranslationServer", stub);
      this.logger.log("TranslationServer bound in registry");
      System.out.println("TranslationServer bound in registry");
      this.logger.log("TranslationServer is running...");
      System.out.println("TranslationServer is running...");
      boolean isRunning = true;
      while (isRunning) {
        ClientRequestHandler requestHandler = new ClientRequestHandler(this, );
        executor.submit();
      }
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
      return "FAIL: the translation for " + key + " already exists";
    } else {
      this.dictionary.put(key.toLowerCase(), value.toLowerCase());
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
    } else {
      translation = "FAIL: I don't know the translation for " + "\"" + key + "\"" + " yet";
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
      return "SUCCESS";
    } else {
      return "FAIL: " + "\"" + key + "\" " + "does not exist";
    }
  }

  /**
   * Stops the server.
   *
   * @throws RemoteException the RMI failure
   */
  @Override
  public void shutdown() throws RemoteException {

  }
}
