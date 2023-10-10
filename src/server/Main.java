package server;

import java.rmi.RemoteException;

public class Main {
  public static void main(String[] args) {
    try {
      IServer server = new Server();
    } catch (RemoteException re) {
      System.out.println("IServer constructor: " + re.getMessage());
      re.printStackTrace();
    }
  }
}