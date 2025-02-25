package cookbook.auth;

import java.sql.*;

import cookbook.db.DB;
import cookbook.db.User;
import org.mindrot.jbcrypt.BCrypt;

public class Auth {
  public static User _currentUser;

  /**
   * Get the current user.
   * @return The current user.
   */
  public static User currentUser() {
    // We can just return the real reference here, because all the setters
    // in the User class are also updating the database with the new values
    // and the fields in the User class are private, so you have to use the
    // setters
    return _currentUser;
  }

  /**
   * Login the user with the given email and password.
   * @param userName The username.
   * @param password The password.
   * @return The login return status.
   */
  public static LoginReturn login(String userName, String password) {
    // Query the database for the user with the given email
    String selectSql = "SELECT * FROM user WHERE userName = ? LIMIT 1";

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      stmt.setString(1, userName);
      ResultSet resultSet = stmt.executeQuery();

      if (!resultSet.next()) {
        // User not found
        System.out.println("Invalid username or password.");
        return LoginReturn.INVALID_USERNAME; 
      }
      // User found, verify the password
      String hashedPassword = resultSet.getString("password");
      if (!BCrypt.checkpw(password, hashedPassword)) {
        System.out.println("Invalid email or password.");
        return LoginReturn.INVALID_PASSWORD;
      }
      // Password matches, set currentUser
      _currentUser = User.fromResultSet(resultSet);
      return LoginReturn.SUCCESS;
    } catch (SQLException e) {
      e.printStackTrace();
      return LoginReturn.SQL_ERROR;
    } catch (Exception e) {
      e.printStackTrace();
      return LoginReturn.UNKNOWN_ERROR;
    }
  }

  /**
   * Logout the current user.
   */
  public static void logout() {
    _currentUser = null;
  }
}
