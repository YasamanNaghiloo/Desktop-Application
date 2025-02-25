package cookbook.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import cookbook.PropertiesReader;

public class DB {
  private static DB instance;
  private Connection conn;

  public static DB getInstance() {
    if (instance == null) {
      instance = new DB();
      instance.init();
    }
    return instance;
  }

  public Connection getConnection() {
    return conn;
  }

  private void init() {
    try {
      String url = new PropertiesReader().getProperty(System.getProperty("user.dir") + "/src/main/resources/credentials.properties", "url");
      conn = DriverManager.getConnection(url);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}