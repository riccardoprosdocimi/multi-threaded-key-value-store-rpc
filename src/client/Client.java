package client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

import server.IServer;
import utils.ILogger;

public class Client {
  private final Scanner scanner = new Scanner(System.in);
  private ILogger logger;
  private static IServer server = null;

  public Client() {
    try {
      server = (IServer)Naming.lookup("rmi://localhost/IServer");
    } catch (Exception e) {
      //this.logger.log("Client: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void print() {
    try {
      System.out.println(server.print());
    } catch (RemoteException e) {
    }
  }
}
