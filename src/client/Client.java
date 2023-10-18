package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import server.IServer;
import utils.ILogger;
import utils.Logger;

public class Client implements IClient {
  private Scanner scanner;
  private ILogger logger;
  private IServer server;

  public Client() {
    try {
      this.logger = new Logger("ClientLogger", "ClientLog.log");
      this.scanner = new Scanner(System.in);
      Registry registry = LocateRegistry.getRegistry("TranslationServer", 1099);
      this.server = (IServer) registry.lookup("TranslationServer");
    } catch (Exception e) {
      this.logger.log("Client error: " + e.getMessage());
      System.err.println("Client error: " + e.getMessage());
      e.printStackTrace();
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
    } catch (RemoteException e) {
      this.logger.log("Server error (pre-populate): " + e.getMessage());
      System.err.println("Server error (pre-populate): " + e.getMessage());
      e.printStackTrace();
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
      this.logger.log("Received malformed request of length " + request.length() + " from the user");
      return "FAIL: please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
    } else {
      String operation;
      try {
        operation = elements[0]; // PUT/GET/DELETE
      } catch (Exception e) {
        this.logger.log("Parsing error: operation. Request of length " + request.length() + " from the user");
        return "FAIL: could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
      }
      String key;
      try {
        key = elements[1]; // word to be translated
      } catch (Exception e) {
        this.logger.log("Parsing error: key. Request of length " + request.length() + " from the user");
        return "FAIL: could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
      }
      String value;
      try {
        switch (operation.toUpperCase()) {
          case "PUT":
            try {
              value = elements[2]; // word to translate
              this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\" from the user");
            } catch (Exception e) {
              this.logger.log("Parsing error: value. Request of length " + request.length() + " from the user");
              return "FAIL: could not parse the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
            }
            result = this.server.put(key, value);
            break;
          case "GET":
            result = this.server.get(key);
            if (result.startsWith("FAIL:")) {
              this.logger.log("Received a request to retrieve the value mapped to a nonexistent key " + "\"" + key + "\" " + "from the user");
            } else {
              this.logger.log("Received a request to retrieve the value mapped to " + "\"" + key + "\" " + "from the user");
            }
            break;
          case "DELETE":
            result = this.server.delete(key);
            if (result.startsWith("FAIL:")) {
              this.logger.log("Received a request to delete a nonexistent key-value pair associated with " + "\"" + key + "\" " + "from the user");
            } else {
              this.logger.log("Received a request to delete the key-value pair associated with " + "\"" + key + "\" " + "from the user");
            }
            break;
          default: // invalid request
            this.logger.log("Received an invalid request: " + request);
            return "Invalid request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
        }
      } catch (RemoteException e) {
        this.logger.log("Server error: " + e.getMessage());
        result = "Server error: " + e.getMessage();
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
    while (isRunning) {
      String request = this.getRequest(); // get the user request
      if (request.equalsIgnoreCase("client shutdown") || request.equalsIgnoreCase("client stop")) { // if the user wants to quit
        isRunning = false; // prepare the shutdown process
      } else if (request.equalsIgnoreCase("server shutdown") || request.equalsIgnoreCase("server stop")) {
        try {
          this.server.shutdown();
        } catch (RemoteException e) {
          this.logger.log("Server error (shutdown): " + e.getMessage());
          System.err.println("Server error (shutdown): " + e.getMessage());
        }
      } else {
        System.out.println(this.parseRequest(request));
      }
    }
    this.shutdown();
  }

  /**
   * Stops the client.
   */
  @Override
  public void shutdown() {
    this.logger.log("Received a request to shut down...");
    System.out.println("Client is shutting down...");
    this.scanner.close();
    this.logger.log("Client stopped");
    this.logger.log("");
    this.logger.close();
    System.out.println("Client closed");
  }
}
