package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class Comment {
  private int commentId;
  private int recipeId;
  private int userId;
  private String content;
  private Date date;

  // If we ever want to serialize this class, we need to make sure that the DB is not serialized
  private transient static final DB db = DB.getInstance();

  private Comment() {

  }

  public int getId() {
    return this.commentId;
  }

  public int getRecipeId() {
    return this.recipeId;
  }

  public int getUserId() {
    return this.userId;
  }

  public String getContent() {
    return this.content;
  }

  public Date getDate() {
    return this.date;
  }

  public static Comment fromDB(int id) {
    String selectSql = "SELECT * FROM cookbook.comment WHERE commentId = ? LIMIT 1";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, id);
      ResultSet resultSet = stmt.executeQuery();

      if (resultSet.next()) {
        return fromResultSet(resultSet);
      }
      return null;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  public static Comment fromResultSet(ResultSet resultSet) throws SQLException {
    Comment comment = new Comment();
    comment.commentId = resultSet.getInt("commentId");
    comment.recipeId = resultSet.getInt("recipeId");
    comment.userId = resultSet.getInt("userId");
    comment.content = resultSet.getString("content");
    comment.date = resultSet.getDate("date");
    return comment;
  }

  public static ArrayList<Comment> getAllFrom (int recipeId) {
    ArrayList<Comment> comments = new ArrayList<Comment>();
    String selectSql = "SELECT * FROM cookbook.comment AS cc WHERE cc.recipeId = ? ";
    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, recipeId);
      try (ResultSet resultSet = stmt.executeQuery()) {
        while (resultSet.next()) {
          comments.add(fromResultSet(resultSet));
        }
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return comments;
  }


  /** 
   * Create a new comment.
   * 
   * @param newContent The new content
   * 
   * @return The new comment
   */
  public void update(String newContent) throws SQLException {
    String updateSql = "UPDATE cookbook.comment SET content = ? WHERE commentId = ? ";
    try (PreparedStatement stmt = db.getConnection().prepareStatement(updateSql)) {
      stmt.setString(1, newContent);
      stmt.setInt(2, this.commentId);
      stmt.executeUpdate();
      this.content = newContent;
    }
  }

  /**
   * Delete the comment.
   * 
   * @throws SQLException
   */
  public void delete() throws SQLException {
    String deleteSql = "DELETE FROM cookbook.comment WHERE commentId = ?";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(deleteSql)) {
      stmt.setInt(1, this.commentId);
      stmt.executeUpdate();
    }
  }
}
