package server;

import java.rmi.RemoteException;

/**
 * The type Main represents the entry point of the RMI server.
 */
public class Main {
  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    if (args.length != 1) { // user should only enter the port number
      System.err.println("Usage: cd in src | then | javac server/*.java utils/*.java | then | java server.Main <Port#>");
    } else {
      try {
        new Server(Integer.parseInt(args[0])); // create and start a new server instance
      } catch (RemoteException re) {
        System.err.println("TranslationServer constructor error: " + re.getMessage());
        re.printStackTrace();
      } catch (NumberFormatException nfe) {
        System.err.println("Port error: " + nfe.getMessage());
        System.err.println("Please enter a valid port number");
      }
    }
  }
}
