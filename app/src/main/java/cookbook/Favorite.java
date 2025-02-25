package cookbook;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cookbook.auth.Auth;
import cookbook.db.DB;
import cookbook.db.Recipe;

public class Favorite {
  
  /**
   * Method for checking if a recipe exists in th eusers favorite list.
   *
   * @param recipeName the name of the recipe to be checked.
   *
   * @param userId the id for the current user in the database.
   *
   * @return returns a boolean value based on if the recipe exists in the list.
   */
  public Boolean checkFavorite(String recipeName, int userId) {
    try {
      int recipeId = Recipe.fromDB(recipeName).getId();
      if (recipeId == -1) {
        return null;
      } else {
        PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("SELECT * FROM favorite WHERE recipeId="
        + recipeId + " AND userId=" + userId + ";");
        ResultSet result = stmt.executeQuery();
        if (result.next()) {
          return true;
        } else {
          return false;
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  /**
   * Method for adding a recipe to the users list of favorite recipes.
   *
   * @param recipeName the name of the recipe.
   *
   * @param userId the id for the current user in the database.
   */
  public void addFavorite(String recipeName, int userId) {
    try {
      int recipeId = Recipe.fromDB(recipeName).getId();
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("INSERT INTO favorite (userId, recipeId) VALUES (?, ?);");
      stmt.setInt(1, Auth.currentUser().getId());
      stmt.setInt(2, recipeId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Method for deleting a recipe from a users favorite list.
   * 
   * @param recipeName name of the recipe to be deleted.
   * 
   *
   * @param userId id of the current user.
   */
  public void deleteFavorite(String recipeName, int userId) {
    try {
      int recipeId = Recipe.fromDB(recipeName).getId();
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("DELETE FROM favorite WHERE recipeId=" + recipeId
      + " AND userId=" + userId + ";");
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
