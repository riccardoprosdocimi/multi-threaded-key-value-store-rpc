package server;

import java.rmi.RemoteException;

public class Main {
  public static void main(String[] args) {
    try {
      new Server();
    } catch (RemoteException e) {
      System.out.println("TranslationServer constructor: " + e.getMessage());
      e.printStackTrace();
    }
  }
}