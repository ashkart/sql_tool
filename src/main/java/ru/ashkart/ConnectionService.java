package ru.ashkart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionService {
  public Connection getLinksConnection() {
    try {
      return DriverManager.getConnection("jdbc:postgresql://localhost:5435/shorter", "shorter", "au34!qzo");
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
      return null;
    }
  }

  public Connection getLinksToRemoveConnection() {
    try {
      return DriverManager.getConnection("jdbc:postgresql://localhost:5432/shorter", "oprosso", "oprosso");
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
      return null;
    }
  }
}
