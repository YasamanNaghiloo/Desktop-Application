package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Data access object for ingredient.
 */
public class IngredientDao {

  /**
   * Get all ingredients.
   *
   * @return The ingredients in the database.
   */
  public ArrayList<cookbook.Ingredient> getIngredients() {
    ArrayList<cookbook.Ingredient> ingredients = new ArrayList<>();

    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT ingredientName, ingredientId, density from ingredient");
      ResultSet res = statement.executeQuery();

      while (res.next()) {
        cookbook.Ingredient ingredient = new cookbook.Ingredient(res.getInt("ingredientId"),
            res.getString("ingredientName"), res.getDouble("density"));
        ingredients.add(ingredient);
      }

    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredients;
  }

  /**
   * Gets an ingredient by the id.
   *
   * @param id The id of the ingredient.
   * @return The ingredient with specified id.
   */
  public cookbook.Ingredient getIngredient(int id) {
    String query = "SELECT ingredientName, density from ingredient where ingredientId = ?";
    cookbook.Ingredient ingredient = null;
    try {

      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setInt(1, id);
      ResultSet res = statement.executeQuery();

      while (res.next()) {
        ingredient = new cookbook.Ingredient(id,
            res.getString("ingredientName"), res.getDouble("density"));
        return ingredient;
      }

    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredient;
  }

  /**
   * Gets the density of an ingredient.
   *
   * @param id The id of the ingredient.
   * @return The density of the ingredient.
   */
  public Double getDensity(int id) {
    String query = "SELECT density from ingredient where ingredientId = ?";
    Double density = null;
    try {

      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setInt(1, id);
      ResultSet res = statement.executeQuery();

      while (res.next()) {
        density = res.getDouble("density");
      }

    } catch (SQLException e) {
      System.err.println(e);
    }
    return density;
  }

  /**
   * Updates the density of an ingredient.
   *
   * @param id The id of the ingredient.
   * @param density The density of the ingredient.
   * @return The density.
   */
  public Double updateDensity(int id, Double density) {
    String query = "UPDATE ingredient SET density = ? WHERE ingredientId = ?";

    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setDouble(1, density);
      statement.setInt(2, id);
      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e);
    }
    return density;
  }

  /**
   * Get all ingredients from the database.
   *
   * @param recipeId The id of the recipe.
   * @return The resultset of all ingredients in the recipe.
   */
  public ResultSet getIngredientsFromDb(int recipeId) {

    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT unitName, unit.unitId, ingredientName, quantity from recipe_ingredient inner join"
          + " ingredient on recipe_ingredient.ingredientId = "
          + "ingredient.ingredientId inner join unit on "
          + "recipe_ingredient.unitId = unit.unitId where "
          + "recipe_ingredient.recipeId ="
              + recipeId);
      ResultSet resultSet = statement.executeQuery();
      return resultSet;
    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  /**
   * Get the id for an ingredient by the name.
   *
   * @param ingredientName The name of the ingredient.
   * @return The id of the ingredient.
   */
  public Integer getIngredientId(String ingredientName) {
    Integer id = null;
    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT ingredientId from ingredient where ingredientName = '"
              + ingredientName + "'");
      ResultSet res = statement.executeQuery();
      while (res.next()) {
        id = (res.getInt("ingredientId"));
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return id;
  }

  public ArrayList<cookbook.Ingredient> checkDensityIfEmpty() {
    ArrayList<cookbook.Ingredient> ingredients = new ArrayList<>();
    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT ingredientName, ingredientId from ingredient WHERE density = NULL");
      ResultSet res = statement.executeQuery();
      while (res.next()) {
        cookbook.Ingredient ingredient = new cookbook.Ingredient(res.getInt("ingredientId"),
            res.getString("ingredientName"), res.getDouble("density"));
        ingredients.add(ingredient);
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredients;
  }
}
