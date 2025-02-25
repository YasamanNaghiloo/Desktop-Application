package cookbook;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cookbook.auth.Auth;
import cookbook.db.DB;

public class OtherUsers {

  public int getUserId(String userName) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection()
          .prepareStatement("SELECT userId FROM user WHERE displayName=\"" + userName + "\";");
      ResultSet result = stmt.executeQuery();
      result.next();
      return result.getInt("userId");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return -1;
    }
  }

  public ArrayList<String> getAllUsers() {
    try {
      ArrayList<String> names = new ArrayList<>();
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("SELECT displayName FROM user WHERE userId!=" + Auth.currentUser().getId() + ";");
      ResultSet result = stmt.executeQuery();
      while (result.next()) {
        names.add(result.getString("displayName"));
      }
      return names;
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      return null;
    }
  }
}
