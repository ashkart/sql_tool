package ru.oprosso;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LinkService {
  private static final String DELETE_STRING_TEMPLATE = "delete from links where id = %d;";

  public void deleteLink(Connection connectionLinks, long linkId) {
    var query = String.format(DELETE_STRING_TEMPLATE, linkId);

    try (Statement stmt = connectionLinks.createStatement()) {
      stmt.execute(query);

      connectionLinks.commit();
    } catch (SQLException sqlException) {
        try {
          connectionLinks.rollback();
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        }
        sqlException.printStackTrace();
    }
  }
}
