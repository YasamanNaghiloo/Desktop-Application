package cookbook;

import java.sql.*;
import java.util.ArrayList;

import org.mindrot.jbcrypt.BCrypt;

import cookbook.auth.Auth;
import cookbook.db.DB;
import cookbook.db.User;

public class Admin {

  private Admin() {
  }

  public static void addUser(String userName, String displayName, String password) {
    if (!Auth.currentUser().isAdmin()) return;
    try (
        // Create prepared statement with the SQL query
        PreparedStatement pstmt = DB.getInstance().getConnection()
            .prepareStatement("INSERT INTO user (userName, password, displayName, admin, imagePath) VALUES (?, ?, ?, 0, Default)");) {
      var encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

      pstmt.setString(1, userName);
      pstmt.setString(2, encryptedPassword);
      pstmt.setString(3, displayName);

      // Execute the SQL query to insert the user
      pstmt.executeUpdate();

      System.out.println("User added successfully.");
    } catch (SQLException e) {
      // Handle any SQL errors
      e.printStackTrace();
    }
  }

  public static void modifyUser(int userId, String userName, String displayName) {
    if (!Auth.currentUser().isAdmin())
      return;
    try (
        // Create prepared statement with the SQL query
        PreparedStatement pstmt = DB.getInstance().getConnection().prepareStatement(
            "update user set userName = ? , displayName = ? where userId = ?");) {
      // var encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

      pstmt.setString(1, userName);
      pstmt.setString(2, displayName);
      pstmt.setString(3, Integer.toString(userId));
      // pstmt.setString(4, isAdmin ? "1" : "0");

      // Execute the SQL query to insert the user
      pstmt.executeUpdate();

      System.out.println("User modified successfully.");
    } catch (SQLException e) {
      // Handle any SQL errors
      e.printStackTrace();
    }
  }

  public static void deleteUser(String userName) {
    if (!Auth.currentUser().isAdmin())
      return;
    try (
        // Create prepared statement with the SQL query
        PreparedStatement pstmt = DB.getInstance().getConnection().prepareStatement("DELETE FROM user WHERE userName = ?");) {
      pstmt.setString(1, userName);

      // Execute the SQL query to delete the user
      pstmt.executeUpdate();

      System.out.println("User deleted successfully.");
    } catch (SQLException e) {
      // Handle any SQL errors
      e.printStackTrace();
    }
  }

  public static ArrayList<User> getUsers() {
    ArrayList<User> users = new ArrayList<User>();
    if (Auth.currentUser().isAdmin()) {
      try (
        // Create prepared statement with the SQL query
        PreparedStatement pstmt = DB.getInstance().getConnection().prepareStatement("select * from user");) {
        try (ResultSet resultSet = pstmt.executeQuery()) {
          while (resultSet.next()) {
            User currentUser = User.fromResultSet(resultSet);
            users.add(currentUser);
          }
        }
      } catch (SQLException e) {
        // Handle any SQL errors
        e.printStackTrace();
      }
    }
    return users;
  }
}