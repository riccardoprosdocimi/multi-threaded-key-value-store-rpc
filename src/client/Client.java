package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import server.IServer;
import utils.ILogger;
import utils.Logger;

/**
 * The type Client represents a client that communicates via Remote Method Invocation (RMI).
 */
public class Client implements IClient {
  private Scanner scanner;
  private ILogger logger;
  private IServer server;

  /**
   * Instantiates a new Client.
   *
   * @param port the port number
   */
  public Client(int port) {
    try {
      this.logger = new Logger("ClientLogger", "ClientLog.log"); // instantiate a logging system that already is thread-safe
      this.scanner = new Scanner(System.in); // instantiate an object to get user input
      Registry registry = LocateRegistry.getRegistry("localhost", port); // get the server's registry
      this.server = (IServer) registry.lookup("//localhost/TranslationServer"); // locate the remote object
    } catch (Exception e) {
      this.logger.log("Client error: " + e.getMessage());
      System.err.println("Client error: " + e.getMessage());
      this.scanner.close();
      this.logger.close();
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Pre-populates the key-value store.
   */
  @Override
  public void prePopulate() {
    try {
      this.logger.log("Pre-populating...");
      System.out.println("Pre-populating...");
      System.out.println(this.server.put("hello", "ciao"));
      System.out.println(this.server.put("goodbye", "addio"));
      System.out.println(this.server.put("thank you", "grazie"));
      System.out.println(this.server.put("please", "per favore"));
      System.out.println(this.server.put("yes", "sì"));
      System.out.println(this.server.put("no", "no"));
      System.out.println(this.server.put("water", "acqua"));
      System.out.println(this.server.put("food", "cibo"));
      System.out.println(this.server.put("friend", "amico"));
      System.out.println(this.server.put("love", "amore"));
      this.logger.log("Pre-population completed");
      System.out.println("Pre-population completed");
      Thread.sleep(1000); // wait a second before user interaction
    } catch (RemoteException re) {
      this.logger.log("TranslationServer error (pre-populate): " + re.getMessage());
      System.err.println("TranslationServer error (pre-populate): " + re.getMessage());
      re.printStackTrace();
    } catch (InterruptedException ie) {
      this.logger.log("Pre-population error (timeout interrupted): " + ie.getMessage());
      ie.printStackTrace();
    }
  }

  /**
   * Gets the user request.
   *
   * @return the user request
   */
  @Override
  public String getRequest() {
    System.out.print("Enter operation (PUT/GET/DELETE:key:value[only with PUT]): ");
    return this.scanner.nextLine();
  }

  private String parseRequest(String request) {
    String result;
    String[] elements = request.split(":");
    if (elements.length < 2 || elements.length > 3) { // the protocol is not followed
      this.logger.log("Received malformed request from the user: " + request);
      return "FAIL: please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
    } else {
      String operation;
      try {
        operation = elements[0]; // PUT/GET/DELETE
      } catch (Exception e) {
        this.logger.log("Parsing error: invalid operation");
        return "FAIL: could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
      }
      String key;
      try {
        key = elements[1]; // word to be translated
      } catch (Exception e) {
        this.logger.log("Parsing error: invalid key");
        return "FAIL: could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
      }
      String value;
      try {
        switch (operation.toUpperCase()) {
          case "PUT":
            try {
              value = elements[2]; // word to translate
              this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\"");
            } catch (Exception e) {
              this.logger.log("Parsing error: invalid value");
              return "FAIL: could not parse the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
            }
            result = this.server.put(key, value);
            break;
          case "GET":
            result = this.server.get(key);
            if (result.startsWith("FAIL:")) {
              this.logger.log("Received a request to retrieve the value mapped to a nonexistent key: \"" + key + "\"");
            } else {
              this.logger.log("Received a request to retrieve the value mapped to \"" + key + "\"");
            }
            break;
          case "DELETE":
            result = this.server.delete(key);
            if (result.startsWith("FAIL:")) {
              this.logger.log("Received a request to delete a nonexistent key-value pair associated with the key: \"" + key + "\"");
            } else {
              this.logger.log("Received a request to delete the key-value pair associated with the key: \"" + key + "\"");
            }
            break;
          default: // invalid request
            this.logger.log("Received an invalid request: " + request);
            return "Invalid request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
        }
      } catch (RemoteException e) { // RMI failure
        this.logger.log("TranslationServer error: " + e.getMessage());
        result = "TranslationServer error: " + e.getMessage();
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * Starts the client.
   */
  @Override
  public void execute() {
    boolean isRunning = true;
    this.logger.log("Client is running...");
    while (isRunning) { // keep getting user input
      String request = this.getRequest(); // get the user request
      if (request.equalsIgnoreCase("shutdown") || request.equalsIgnoreCase("stop")) { // if the user wants to quit
        isRunning = false; // prepare the shutdown process
      } else {
        System.out.println(this.parseRequest(request)); // process the request
      }
    }
    this.shutdown(); // shut down both the server and the client
  }

  /**
   * Stops the client and the server.
   */
  @Override
  public void shutdown() {
    this.logger.log("Received a request to shut down...");
    System.out.println("Client is shutting down...");
    try {
      this.server.shutdown(); // shut down the server
      this.logger.log("TranslationServer shut down");
    } catch (RemoteException e) {
      this.logger.log("TranslationServer error (shutdown): " + e.getMessage());
      System.err.println("TranslationServer error (shutdown): " + e.getMessage());
    }
    this.scanner.close();
    this.logger.log("Client closed");
    this.logger.close();
    System.out.println("Client closed");
  }
}
