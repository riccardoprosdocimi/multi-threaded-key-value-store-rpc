package server;

import java.rmi.RemoteException;

public class ClientRequestHandler implements Runnable {
  private final IServer server;
  private final String requestType;
  private final String key;
  private final String value = null;
  private String result;

  public ClientRequestHandler(IServer server, String requestType, String key, String value) {
    this.server = server;
    this.requestType = requestType;
    this.key = key;
    this.value = value;
  }

  public ClientRequestHandler(IServer server, String requestType, String key) {
    this.server = server;
    this.requestType = requestType;
    this.key = key;
  }

  /**
   * Runs this operation.
   */
  @Override
  public void run() {
    try {
      switch (this.requestType.toLowerCase()) {
        case "put":
          this.result = this.server.put(this.key, this.value);
          break;
        case "get":
          this.result = this.server.get(this.key);
          break;
        case "delete":
          this.result = this.server.delete(this.key);
          break;
        default:
          this.result = "Unsupported request type";
          break;
      }
    } catch (RemoteException e) {
      this.result = "Execution error: " + e.getMessage();
      e.printStackTrace();
    }
  }

  public String getResult() {
    return this.result;
  }
}
