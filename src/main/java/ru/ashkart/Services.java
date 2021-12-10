package ru.ashkart;

public final class Services {
  private static Services container = null;

  private static ConnectionService connectionService = null;
  private static LinkService linkService = null;

  private Services() {

  }

  public static Services getContainer() {
    if (container == null) {
      container = new Services();
    }

    return container;
  }

  public ConnectionService getConnectionService() {
    if (connectionService == null) {
      connectionService = new ConnectionService();
    }

    return connectionService;
  }

  public LinkService getLinkService() {
    if (linkService == null) {
      linkService = new LinkService();
    }

    return linkService;
  }
}
