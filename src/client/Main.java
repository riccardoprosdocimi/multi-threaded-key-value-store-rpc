package client;

/**
 * The type Main represents the entry point of the RMI client.
 */
public class Main {
  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    if (args.length != 1) { // user should only enter the port number
      System.err.println("Usage: cd in src | then | javac client/*.java utils/*.java | then | java client.Main <Port#>");
    } else {
      try {
        Client client = new Client(Integer.parseInt(args[0])); // parse the port number as an integer
        client.prePopulate(); // pre-populate the server
        client.execute(); // start the interactive client
      } catch (NumberFormatException e) {
        System.err.println("Port error: " + e.getMessage());
        System.err.println("Please enter a valid port number");
      }
    }
  }
}
