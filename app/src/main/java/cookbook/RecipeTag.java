package cookbook;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cookbook.db.DB;

public class RecipeTag {

  public int getTagId(String tag) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("SELECT tagId FROM tag WHERE tagName=\"" + tag + "\";");
      ResultSet result = stmt.executeQuery();
      result.next();
      return result.getInt("tagId");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return -1;
  }

  public boolean checkTag(String tag) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("SELECT tagId FROM tag WHERE tagName=\"" + tag + "\";");
      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return true;
      } else {
        return false;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  public void addTag(String tag) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("INSERT INTO tag VALUES (Default, ?);");
      stmt.setString(2, tag);
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void addTagToRecipe(int recipeId, int tagId) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("INSERT INTO recipe_tag VALUES (?, ?);");
      stmt.setInt(1, recipeId);
      stmt.setInt(2, tagId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void removeTagFromRecipe(int recipeId, int tagId) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection()
          .prepareStatement("DELETE FROM recipe_tag WHERE recipeId=" + recipeId + " AND tagId=" + tagId + ";");
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
