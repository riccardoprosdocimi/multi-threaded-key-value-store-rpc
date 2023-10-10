package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements IServer{
  public Server() throws RemoteException {
    super();
    try {
      LocateRegistry.createRegistry(1099);
      Naming.rebind("rmi://localhost/IServer", this);
      System.out.println("IServer bound in registry");
    } catch (MalformedURLException e) {
      System.out.println("Rebind: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public String print() throws RemoteException {
    System.out.println("Invocation to print was successful!");
    return "Hello from server!";
  }
}
