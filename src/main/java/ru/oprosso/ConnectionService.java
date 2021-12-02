package ru.oprosso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionService {
  public Connection getConnection() {
    try {
      return DriverManager.getConnection("jdbc:postgresql://localhost:5432/shorter", "oprosso", "oprosso");
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
      return null;
    }
  }
}
