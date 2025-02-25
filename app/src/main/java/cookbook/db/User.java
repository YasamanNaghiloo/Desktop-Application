package cookbook.db;


import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;
import java.util.ArrayList;

import org.mindrot.jbcrypt.BCrypt;

public class User {
  private int id;
  private String username;
  private String displayName;
  private boolean admin;
  private String imagePath;

  // private constructor to prevent instantiation outside of the class
  private User() {}

  /**
   * User Factory Method to fetch user from database by ID
   */
  public static User fromDb(int id) throws SQLException {
    String selectSql = "SELECT * FROM user WHERE userId = ? LIMIT 1"; // Using PreparedStatement to prevent SQL injection

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, id);
      ResultSet resultSet = stmt.executeQuery();

      if (resultSet.next()) {
        return fromResultSet(resultSet);
      }
      return null;
    }
  }

  /**
   * User Factory Method to load the values from a ResultSet
   * 
   * @param resultSet ResultSet
   * 
   * @return User object
   */
  public static User fromResultSet(ResultSet resultSet) throws SQLException {
    User user = new User();
    user.id = resultSet.getInt("userId");
    user.username = resultSet.getString("userName");
    user.displayName = resultSet.getString("displayName");
    user.admin = resultSet.getBoolean("admin");
    user.imagePath = resultSet.getString("imagePath");
    return user;
  }

  /**
   * Get the user's id
   * 
   * @return id
   */
  public int getId() {
    return this.id;
  }

  /**
   * Get the user's username
   * 
   * @return name
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Get the user's display name
   * 
   * @return lastName
   */
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * Get the user's image path
   * 
   * @return imagePath
   */
  public String getImagePath() {
    return this.imagePath;
  }

  /**
   * Get the user's admin status
   * 
   * @return admin
   */
  public boolean isAdmin() {
    return this.admin;
  }

  public static ArrayList<User> getAll() throws SQLException {
    String selectSql = "SELECT * FROM user;"; // Using PreparedStatement to prevent SQL injection

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<User> names = new ArrayList<>();

      while (resultSet.next()) {
        names.add(fromResultSet(resultSet));
      }
      return names;
    }
  }

  public static ArrayList<User> getAllLike(String like) throws SQLException {
    String selectSql = "SELECT * FROM user WHERE displayName LIKE ?";

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      stmt.setString(1, "%" + like + "%");
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<User> names = new ArrayList<>();

      while (resultSet.next()) {
        names.add(fromResultSet(resultSet));
      }
      return names;
    }
  }

  /**
   * Set the user's display name
   * 
   * @param displayName
   */
  public void setDisplayName(String displayName) throws SQLException {
    this.displayName = displayName;
    String updateSql = "UPDATE user SET displayName = ? WHERE userId = ?";

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(updateSql)) {
      stmt.setString(1, displayName);
      stmt.setInt(2, this.id);
      stmt.executeUpdate();
    }
  }

  /**
   * Set the user's username
   * 
   * @param username
   */
  public void setUsername(String username) throws SQLException {
    this.username = username;
    String updateSql = "UPDATE user SET userName = ? WHERE userId = ?";

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(updateSql)) {
      stmt.setString(1, username);
      stmt.setInt(2, this.id);
      stmt.executeUpdate();
    }
  }

  /**
   * Set the user's image path
   * 
   * @param imagePath
   */
  public void setImagePath(String imagePath) throws SQLException {
    this.imagePath = imagePath;
    String updateSql = "UPDATE user SET imagePath = ? WHERE userId = ?";

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(updateSql)) {
      stmt.setString(1, imagePath);
      stmt.setInt(2, this.id);
      stmt.executeUpdate();
    }
  }

  /**
   * Set the user's password
   * 
   * @param password
   */
  public void setPassword(String password) throws SQLException {
    String updateSql = "UPDATE user SET password = ? WHERE userId = ?";

    var encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(updateSql)) {
      stmt.setString(1, encryptedPassword);
      stmt.setInt(2, this.id);
      stmt.executeUpdate();
    }
  }
}
