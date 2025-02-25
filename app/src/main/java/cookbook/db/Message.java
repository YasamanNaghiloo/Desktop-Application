package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents a message in the database.
 */
public class Message {
  private int senderId;
  private int receiverId;
  private String text;
  private int recipeId;

  // If we ever want to serialize this class, we need to make sure that the DB is
  // not serialized
  private transient static final DB db = DB.getInstance();

  /**
   * Constructor for factory.
   * 
   */
  private Message() {

  }

  /*public int getMessageId() {
    return this.messageId;
  }*/

  public int getSenderId() {
    return this.senderId;
  }

  public int getReceiverId() {
    return this.receiverId;
  }

  public String getMessageText() {
    return this.text;
  }

  /*public Date getDate() {
    return this.date;
  }*/

  public int getRecipeId() {
    return this.recipeId;
  }

  /*public boolean getIsRead() {
    return this.isRead;
  }*/

  /**
   * Get a message from the database.
   * 
   * @param resultSet is from prev query.
   * @return The message with the given name.
   */
  public static Message fromResultSet(ResultSet resultSet) throws SQLException {
    Message message = new Message();
    message.senderId = resultSet.getInt(1);
    message.receiverId = resultSet.getInt(2);
    message.recipeId = resultSet.getInt(3);
    message.text = resultSet.getString(4);
    return message;
  }

  public static ArrayList<Message> getAllTo(int userId) {
    String selectSql = "SELECT * FROM cookbook.message AS cm WHERE cm.recieverUserId = ? ;";
    ArrayList<Message> messages = new ArrayList<Message>();
    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, userId);
      try (ResultSet resultSet = stmt.executeQuery()) {
        while (resultSet.next()) {
          messages.add(fromResultSet(resultSet));
        }
      }
    } catch (SQLException e) {
      System.err.println("An error has occured: " + e);
    }
    return messages;
  }

  /**
   * This will return messages both for reciever and sender.
   * 
   * @param userId is the userId on both sides.
   * @return an ArrayList of Message.
   */
  public static ArrayList<Message> getAllFromUnread(int userId) {
    String q = "SELECT * FROM cookbook.message AS cm WHERE cm.isRead = 0 AND cm.recieverUserId = ? ";
    ArrayList<Message> messages = new ArrayList<Message>();
    try (PreparedStatement stmt = db.getConnection().prepareStatement(q)) {
      stmt.setInt(1, userId);
      try (ResultSet resultSet = stmt.executeQuery()) {
        while (resultSet.next()) {
          messages.add(fromResultSet(resultSet));
        }
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return messages;
  }

  public static ArrayList<Message> getAllFromRead(int userId) {
    String q = "SELECT * FROM cookbook.message AS cm WHERE cm.isRead = 1 AND cm.recieverUserId = ? ";
    ArrayList<Message> messages = new ArrayList<Message>();
    try (PreparedStatement stmt = db.getConnection().prepareStatement(q)) {
      stmt.setInt(1, userId);
      try (ResultSet resultSet = stmt.executeQuery()) {
        while (resultSet.next()) {
          messages.add(fromResultSet(resultSet));
        }
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return messages;
  }

  public static Message fromDB(int id) {
    String query = "SELECT * FROM cookbook.message WHERE messageId = ?";
    try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
      stmt.setInt(1, id);
      try (ResultSet resultSet = stmt.executeQuery()) {
        if (resultSet.next()) {
          return fromResultSet(resultSet);
        }
      }
    } catch (SQLException e) {
      System.err.println("Failed to retrieve message: " + e);
    }
    return null;
  }

  /**
   * ----------------- "CRUD" methods -----------------
   */

  /** It must be checked if A user has right (only sender) to delete that message.
   * 
   * @param message to be deleted.
   */
  public void delete() {
    String q = "DELETE FROM cookbook.message WHERE senderUserId = ? AND recieverUserId = ? AND recipeId = ?";
    try (PreparedStatement stmt = db.getConnection().prepareStatement(q)) {
      stmt.setInt(1, this.senderId);
      stmt.setInt(receiverId, 2);
      stmt.setInt(recipeId, 3);
      int rowAffected = stmt.executeUpdate();
      if (rowAffected > 0) {
        System.out.println("The message was successfuly deleted. ");
      } else {
        System.out.println("Couldn't delete the message. ");
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
  }

  /* public void markAsRead() {
    String query = "UPDATE cookbook.message SET isRead = ? WHERE messageId = ?";
    try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
      stmt.setBoolean(1, this.isRead);
      stmt.setInt(2, this.messageId);
      int rowAffected = stmt.executeUpdate();
      if (rowAffected == 0) {
        System.err.println("Failed to update the message as read in the database.");
      }
      this.isRead = true;
    } catch (SQLException e) {
      System.err.println("Database update error: " + e);
    }
  }*/

  @Override
  public String toString() {
    return this.text;
  }
}
