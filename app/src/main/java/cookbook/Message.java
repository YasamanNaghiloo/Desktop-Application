package cookbook;

import cookbook.auth.Auth;
import cookbook.db.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Message {
  private int userId;
  private int messageId;

  public Message() {
    this.userId = Auth.currentUser() != null ? Auth.currentUser().getId() : -1;

  }

  /** It is a method in order to insert a message into the database.

   * @param recipeId is the recipeId.
   * @param receiverId is the receiverId.
   * @param text is the content.
   */
  public void sendMessage(int recipeId, int receiverId, String text) {
    String q = "INSERT INTO message VALUES (?, ?, ?, ?);";
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(q);
      stmt.setInt(1, Auth.currentUser().getId());
      stmt.setInt(2, receiverId);
      stmt.setInt(3, recipeId);
      stmt.setString(4, text);
      stmt.executeUpdate();
      System.out.println("Successfully added message");
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  //  * @param messageId is the messageID.
  //  * @param receiverId is receiverId.
  //  */
  // public void addToNotification(int messageId, int receiverId) {
  //   String query = "INSERT INTO cookbook.notifications (reciever_id, message_id) ";
  //   query += "VALUES (?, ?)";
  //   try (PreparedStatement ps = Database.conn.prepareStatement(query)) {
  //     ps.setInt(1, receiverId);
  //     ps.setInt(2, messageId);
  //   } catch (SQLException e) {
  //     System.err.println(e);
  //   }
  // }

  // public boolean readMessage(int userId) {
  
  // }

  public ResultSet retrieveMessage() {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("SELECT * FROM message WHERE receiverId = " + Auth.currentUser().getId() + ";");
      ResultSet result = stmt.executeQuery();
      return result;
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      return null;
    }
  }

  // public int hadUnreadMessage(int userId) {

  // }
}
