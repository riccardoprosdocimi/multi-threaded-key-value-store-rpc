package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.ILogger;

/**
 * The interface server contains methods that all types of translation servers should support.
 */
public interface IServer extends Remote {
  /**
   * Sets the port number.
   *
   * @param port the port
   */
  //void setPortNumber(String port);

  /**
   * Sets the logger.
   *
   * @param logger the logger
   * @exception RemoteException the RMI failure
   */
  void setLogger(ILogger logger) throws RemoteException;

  /**
   * Sets the translation service.
   *
   * @param translationService the translation service
   * @exception RemoteException the RMI failure
   */
  void setTranslationService(ITranslationService translationService) throws RemoteException;

  /**
   * Starts the server.
   */
  //void execute();

  /**
   * Stops the server.
   */
  //void shutdown();
}
