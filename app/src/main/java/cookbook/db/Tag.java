package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Tag {
  private int id;
  private String name;

  // If we ever want to serialize this class, we need to make sure that the DB is not serialized
  private transient static final DB db = DB.getInstance();

  /**
   * Private constructor to only allow Factories.
   */
  private Tag() {}

  /**
   * Get the ID of the tag.
   * 
   * @return The ID of the tag.
   */
  public int getId() {
    return id;
  }

  /**
   * Get the name of the tag.
   * 
   * @return The name of the tag.
   */
  public String getName() {
    return name;
  }

  /**
   * Get a tag from the database.
   * 
   * @param id The ID of the tag.
   * @return The tag with the given ID.
   */
  public static Tag fromDB(int id) {
    String selectSql = "SELECT * FROM tag WHERE tagId = ? LIMIT 1";

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

  public static ArrayList<Tag> fromRecipe(int recipeId) {
    String selectSql = "SELECT * FROM tag WHERE tagId IN (SELECT tagId FROM recipe_tag WHERE recipeId = ?)";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, recipeId);
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Tag> tags = new ArrayList<>();

      while (resultSet.next()) {
        tags.add(fromResultSet(resultSet));
      }
      return tags;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Get a tag from the database.
   * 
   * @param name The name of the tag.
   * @return The tag with the given name.
   */
  public static Tag fromResultSet(ResultSet resultSet) throws SQLException {
    Tag tag = new Tag();
    tag.id = resultSet.getInt("tagId");
    tag.name = resultSet.getString("tagName");
    return tag;
  }

  /**
   * Get all tags from the database.
   * 
   * @return An ArrayList of all tags.
   */
  public static ArrayList<Tag> getAll() {
    String selectSql = "SELECT * FROM tag"; // Using PreparedStatement to prevent SQL injection

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Tag> tags = new ArrayList<>();

      while (resultSet.next()) {
        tags.add(fromResultSet(resultSet));
      }
      return tags;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Get all tags that are like the given string.
   * 
   * @param like The string to search for.
   * @return An ArrayList of all tags that are like the given string.
   */
  public static ArrayList<Tag> getAllLike(String like) {
    String selectSql = "SELECT * FROM tag WHERE tagName LIKE ?";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setString(1, "%" + like + "%");
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Tag> tags = new ArrayList<>();

      while (resultSet.next()) {
        tags.add(fromResultSet(resultSet));
      }
      return tags;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  @Override
  public String toString() {
    return name;
  }
}