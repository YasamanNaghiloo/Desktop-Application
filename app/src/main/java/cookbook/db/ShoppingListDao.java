package cookbook.db;

import cookbook.TripleTuple;
import cookbook.Unit;
import cookbook.auth.Auth;
import cookbook.searcher.SearchInFile;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data access object for shopping list.
 */
public class ShoppingListDao {
  /**
   * Delete ingredient from shoppinglist.
   *
   * @param ingredientId The id of the ingredient.
   * @param unitId The id of the unit.
   */
  public void deleteFromDatabase(int ingredientId, int unitId) {
    try {
      String query = "DELETE From shopping_list where userId = ? and ingredientId = ?"
          + " and unitId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(query);
      statement.setInt(1, Auth.currentUser().getId());
      statement.setInt(2, ingredientId);
      statement.setInt(3, unitId);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e);
    }
  }

  /**
   * Get ids of all ingredients in the shopping list.
   *
   * @return The ids of the ingredients in the shopping list.
   */
  public ArrayList<Integer> getIngredientIdsInList() {
    try {
      String query = "SELECT ingredientId from shopping_List where userId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setInt(1, Auth.currentUser().getId());
      ResultSet resultSet = statement.executeQuery();
      ArrayList<Integer> ingredientIds = new ArrayList<>();
      while (resultSet.next()) {
        ingredientIds.add(resultSet.getInt("ingredientId"));
      }
      return ingredientIds;
    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  /**
   * Get specific ingredient from shoppinglist.
   *
   * @param ingredientId The id of the ingredient.
   * @return The ingredient.
   */
  public ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> 
      getIngredientInList(int ingredientId) {
    ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> ingredientItems = new ArrayList<>();
    try {
      String query = "SELECT shopping_list.quantity, unit.unitId, unit.unitName, "
          + "ingredient.ingredientName, ingredient.density from shopping_List inner join unit on"
          + " shopping_list.unitId"
          + " = unit.unitId inner join ingredient on shopping_list.ingredientId ="
          + " ingredient.ingredientId where shopping_list.ingredientId = ? and shopping_list.userId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setInt(1, ingredientId);
      statement.setInt(2, Auth.currentUser().getId());
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        TripleTuple<cookbook.Ingredient, Double, Unit> t = new TripleTuple<>(
            new cookbook.Ingredient(ingredientId, resultSet.getString("ingredientName"),
                resultSet.getDouble("density")),
            resultSet.getDouble("quantity"),
            new Unit(resultSet.getInt("unitId"), resultSet.getString("unitName")));
        ingredientItems.add(t);
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredientItems;
  }

  /**
   * Put in an ingredient into the shoppinglist. 
   *
   * @param ingredientTuple The ingredient to add.
   * @param newIngredient Is it a new ingredient.
   */
  public void inputListIngredient(TripleTuple<cookbook.Ingredient, Double, Unit> ingredientTuple,
      boolean newIngredient) {
    // if a new ingredient
    if (newIngredient) {
      try {
        // Insert ingredient
        DatabaseStatement databaseStatement = new DatabaseStatement();
        IngredientDao ingredientDao = new IngredientDao();
        databaseStatement.insert("ingredient",
            new String[] { "ingredientName" },
            new String[] { ingredientTuple.first.getName() },
            false);
        // Get the id of the new ingredient
        int id = ingredientDao.getIngredientId(ingredientTuple.first.getName());
        // Insert the ingredient into the shopping list
        databaseStatement.insert("shopping_list",
            new String[] { "userId", "ingredientId", "quantity", "unitId" },
            new String[] { Integer.toString(Auth.currentUser().getId()),
                Integer.toString(id),
                Double.toString(ingredientTuple.second), Integer
                  .toString(ingredientTuple.third.getId()) },
            false);
      } catch (Exception e) {
        System.err.println(e);
      }
      return;
    }
    SearchInFile searchInFile = new SearchInFile();
    ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> ingredientItems = getIngredientInList(
        ingredientTuple.first.getId());
    TripleTuple<cookbook.Ingredient, Double, Unit> originIngredient = null;
    boolean isConverted = false;
    Double convertedQuantity = null;
    // Check if there are entries of the same ingredient.
    if (ingredientItems.size() > 0) {
      // Cycle through the entries of the same ingredient
      for (int i = 0; i < ingredientItems.size(); i++) {
        IngredientDao ingredientDao = new IngredientDao();
        try {
          // Try to convert the quantity to the quantity that the other entry has
          convertedQuantity = searchInFile.convertToUnit(
              ingredientTuple.first.getName(), ingredientTuple.second,
              ingredientTuple.third, ingredientItems.get(
                  i).third,
              ingredientDao
                  .getDensity(ingredientTuple.first.getId()));
        } catch (IllegalArgumentException e) {
          System.err.println("haaar");
          // Save the ingredient if not possible to convert
          originIngredient = ingredientItems.get(i);
        }
        if (convertedQuantity != null) {
          originIngredient = ingredientItems.get(i);
          isConverted = true;
          // Break, if an entry with a convertable unit is found.
          break;
        }
      }
      // Insert into shoppinglist
      TripleTuple<cookbook.Ingredient, Double, Unit> insertTuple;
      if (convertedQuantity == null) {
        insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
            ingredientTuple.first.getId(), ingredientTuple.first.getName(),
             ingredientTuple.first.getDensity()),
            ingredientTuple.second, new Unit(ingredientTuple.third.getId(),
             ingredientTuple.third.getName()));
        try {
          DatabaseStatement databaseStatement = new DatabaseStatement();
          databaseStatement.insert("shopping_list",
              new String[] { "userId", "ingredientId", "quantity", "unitId" },
              new String[] { Integer.toString(Auth.currentUser().getId()),
                  Integer.toString(insertTuple.first.getId()),
                  Double.toString(insertTuple.second), Integer
                  .toString(insertTuple.third.getId()) },
              false);
        } catch (Exception e) {
          System.err.println(e);
        }
        // If the two entries could not be combined, create new entry.
      } else {
        if (isConverted && originIngredient != null) {
          //System.err.println("It was converted and inserted");
          insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
              ingredientTuple.first.getId(), ingredientTuple.first.getName(),
               ingredientTuple.first.getDensity()),
              convertedQuantity + originIngredient.second,
              new Unit(originIngredient.third.getId(), originIngredient.third.getName()));
          updateIngredientInShoppingList(insertTuple, originIngredient.third);
        } else if (originIngredient != null) {
          //System.err.println("No need for conversion");
          insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
              ingredientTuple.first.getId(), ingredientTuple.first.getName(),
               ingredientTuple.first.getDensity()),
              ingredientTuple.second + originIngredient.second,
              new Unit(originIngredient.third.getId(), originIngredient.third.getName()));
          updateIngredientInShoppingList(insertTuple, ingredientTuple.third);
        } else {
          System.err.println("The originIngredient was not set");
        }
      }
    } else {
      //System.err.println("New ingredient added to shoppingList");
      TripleTuple<cookbook.Ingredient, Double, Unit> insertTuple;
      insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
          ingredientTuple.first.getId(), ingredientTuple.first.getName(),
           ingredientTuple.first.getDensity()),
          ingredientTuple.second,
          new Unit(ingredientTuple.third.getId(), ingredientTuple.third.getName()));
      try {
        DatabaseStatement databaseStatement = new DatabaseStatement();
        databaseStatement.insert("shopping_list",
            new String[] { "userId", "ingredientId", "quantity", "unitId" },
            new String[] { Integer.toString(Auth.currentUser().getId()),
                Integer.toString(insertTuple.first.getId()),
                Double.toString(insertTuple.second), Integer.toString(insertTuple.third.getId()) },
            false);
      } catch (Exception e) {
        System.err.println(e);
      }

    }

  }

  /**
   * Gets ingredients for a recipe into the shopping list.
   *
   * @param recipeId The id of the recipe.
   */
  public void getIngredientsFromRecipetoDb(int recipeId) {
    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT unit.unitName, unit.unitId, ingredient.ingredientId, "
              + "ingredient.ingredientName, ingredient.density, recipe_ingredient.quantity"
              + " from recipe_ingredient "
              + "inner join ingredient on recipe_ingredient.ingredientId"
              + " = ingredient.ingredientId inner join unit on "
              + "recipe_ingredient.unitId = unit.unitId where"
              + " recipe_ingredient.recipeId ="
              + recipeId);
      ResultSet res = statement.executeQuery();
      ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> ingredients = new ArrayList<>();
      while (res.next()) {
        int ingredientId = res.getInt("ingredientId");
        int unitId = res.getInt("unitId");
        String ingredientName = res.getString("ingredientName");
        String unitName = res.getString("unitName");
        Double quantity = res.getDouble("quantity");
        Double density = res.getDouble("density");
        TripleTuple<cookbook.Ingredient, Double, Unit> t = 
            new TripleTuple<cookbook.Ingredient, Double, Unit>(
                new cookbook.Ingredient(ingredientId, ingredientName, density), quantity,
                new Unit(unitId, unitName));
        ingredients.add(t);
      }
      for (TripleTuple<cookbook.Ingredient, Double, Unit> tripleTuple : ingredients) {
        inputListIngredient(tripleTuple, false);
      }

    } catch (SQLException e) {
      System.err.println(e);
    }
  }


  /**
   * Emptys the shoopinglist.
   */
  public void emptyShoppingList() {
    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "DELETE From shopping_list where userId = " + Auth.currentUser().getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e);
    }
  }

  /**
   * Update an ingredient in the shoppinglist.
   *
   * @param ingredient The ingredient to update.
   * @param oldUnit The unit before change.
   */
  public void updateIngredientInShoppingList(TripleTuple<cookbook.Ingredient, Double, Unit> 
      ingredient, Unit oldUnit) {
    try {
      // Create the UPDATE SQL query
      String query = "UPDATE shopping_list SET quantity = ?, unitId = ? WHERE ingredientId"
          + " = ? and userId = ? and unitId = ?";
      // Create a PreparedStatement with the query
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(query);

      // Set values for the parameters in the query
      statement.setDouble(1, ingredient.second);
      statement.setInt(2, ingredient.third.getId());
      statement.setInt(3, ingredient.first.getId());
      statement.setInt(4, Auth.currentUser().getId());
      statement.setInt(5, oldUnit.getId());
      // Set any other parameters if needed

      // Execute the UPDATE statement
      int rowsUpdated = statement.executeUpdate();
      // Check if any rows were updated
      if (rowsUpdated > 0) {
        System.out.println("Database entry updated successfully.");
      } else {
        System.out.println("No rows were updated.");
      }

    } catch (SQLException e) {
      // Handle any errors that may occur
      e.printStackTrace();
    }
  }


  private boolean checkIfIngredientInShoppingList(int ingredientId) {
    try {
      String query = "Select ingredientId From shopping_list where ingredientId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setInt(1, ingredientId);
      ResultSet res = statement.executeQuery();

      if (!res.next()) {
        // ResultSet is empty
        return false;
      } else {
        return true;
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return false;
  }

  /**
   * Get ingredients in the shoppinglist.
   *
   * @return The ingredients in the shopping list.
   */
  public ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> getShoppingListItems() {
    ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> shoppingListItems = new ArrayList<>();
    try {
      int userId = Auth.currentUser().getId();
      String query = "SELECT shopping_list.unitId, unit.unitName, shopping_list.ingredientId,"
          + " ingredient.ingredientName, ingredient.density, shopping_list.quantity "
          + "FROM shopping_list "
          + "INNER JOIN ingredient ON shopping_list.ingredientId ="
          + " ingredient.ingredientId "
          + "INNER JOIN unit ON shopping_list.unitId = unit.unitId "
          + "WHERE shopping_list.userId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(query);
      statement.setInt(1, userId);
      ResultSet res = statement.executeQuery();

      while (res.next()) {
        cookbook.Ingredient ingredient = new cookbook.Ingredient(res.getInt("ingredientId"),
            res.getString("ingredientName"), res.getDouble("density"));
        Double quantity = res.getDouble("quantity");
        Unit unit = new Unit(res.getInt("unitId"), res.getString("unitName"));
        TripleTuple<cookbook.Ingredient, Double, Unit> t = new TripleTuple<>(
            ingredient, quantity, unit);

        shoppingListItems.add(t);

      }
      return shoppingListItems;

    } catch (SQLException e) {
      System.err.println(e);
    }
    return shoppingListItems;
  }
}
