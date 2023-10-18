package client;

import utils.ILogger;

/**
 * The interface client contains methods that all types of clients should support.
 */
public interface IClient {
  /**
   * Gets the user request.
   *
   * @return the user request
   */
  String getRequest();

  /**
   * Pre-populates the key-value store.
   */
  void prePopulate();

  /**
   * Starts the client.
   */
  void execute();

  /**
   * Stops the client.
   */
  void shutdown();
}
