package cookbook.scaling;

import cookbook.TripleTuple;
import cookbook.db.DB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Scales the recipe.
 */
public class ScaleRecipe {

  /**
   * Scales a recipe.
   *
   * @param recipeId The id of the recipe to scale.
   * @param scaleAmount the new portion number.
   * @return The ingredients in the recipe with scaled amounts.
   */
  public ArrayList<TripleTuple<String, Double, String>> 
      getScaleRecipe(int recipeId, int scaleAmount) {
    ArrayList<TripleTuple<String, Double, String>> ingredients = new ArrayList<>();
    try {
      String query = "Select ingredient.ingredientName, recipe_ingredient.quantity,"
          + " unit.unitName, recipe.portion From recipe_ingredient INNER JOIN ingredient"
          + " ON recipe_ingredient.ingredientId = ingredient.ingredientId INNER JOIN unit"
          + " ON recipe_ingredient.unitId = unit.unitId INNER JOIN recipe ON "
          + "recipe_ingredient.recipeId = recipe.recipeId WHERE recipe.recipeId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setInt(1, recipeId);
      ResultSet res = statement.executeQuery();
      while (res.next()) {
        String ingredientName = res.getString("ingredientName");
        Double quantity = res.getDouble("quantity");
        String unit = res.getString("unitName");
        int portion = res.getInt("portion");
        TripleTuple<String, Double, String> ingredient =
             new TripleTuple<String, Double, String>(ingredientName,
            scaler(quantity, portion, scaleAmount), unit);
        ingredients.add(ingredient);
      }
      System.err.println("ingeerrrsize" + ingredients.size());
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredients;
  }

  private Double scaler(Double quantity, int portion, int newPortion) {
    return quantity / portion * newPortion;
  }
}
