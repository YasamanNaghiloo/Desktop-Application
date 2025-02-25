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
 * Data access object for fridge.
 */
public class FridgeDao {

  /**
   * Get ingredients in fridge.
   *
   * @return Ingredients currently in fridge.
   */
  public ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> getFridgeIngredients() {
    ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> ingredientItems = new ArrayList<>();
    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT unit.unitId, unit.unitName, ingredient.ingredientId, ingredient.density,"
              + " ingredient.ingredientName, fridge.quantity "
              + "FROM fridge "
              + "INNER JOIN ingredient ON fridge.ingredientId ="
              + " ingredient.ingredientId "
              + "INNER JOIN unit ON fridge.unitId = unit.unitId "
              + "WHERE fridge.userId = " + Auth.currentUser().getId());
      ResultSet res = statement.executeQuery();

      while (res.next()) {
        cookbook.Ingredient ingredient = new cookbook
            .Ingredient(res.getInt("ingredient.ingredientId"),
            res.getString("ingredient.ingredientName"), res.getDouble("density"));
        Double quantity = res.getDouble("fridge.quantity");
        Unit unit = new Unit(res.getInt("unit.unitId"), res.getString("unit.unitName"));
        TripleTuple<cookbook.Ingredient, Double, Unit> t = new TripleTuple<>(
            ingredient, quantity, unit);

        ingredientItems.add(t);

      }
      return ingredientItems;

    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  /**
   * Gets the ids of the ingredients in the fridge.
   *
   * @return The id's of the ingredients int the fridge.
   */
  public ArrayList<Integer> getIngredientsInList() {
    try {
      String query = "SELECT ingredientId from fridge where userId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setInt(1, Auth.currentUser().getId());
      ResultSet resultSet = statement.executeQuery();
      ArrayList<Integer> ingredientNames = new ArrayList<>();
      while (resultSet.next()) {
        ingredientNames.add(resultSet.getInt("ingredientId"));
      }
      return ingredientNames;
    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  /**
   * Gets ingredient from fridge.
   *
   * @param ingredientId The id for the ingredient.
   *
   * @return The ingredient with the requested id.
   */
  public ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> 
      getIngredientInFridge(int ingredientId) {
    ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> ingredientItems = new ArrayList<>();
    try {
      String query = "SELECT fridge.quantity, unit.unitId, unit.unitName, "
          + "ingredient.ingredientName, ingredient.density from fridge inner join unit on"
          + " fridge.unitId"
          + " = unit.unitId inner join ingredient on fridge.ingredientId ="
          + " ingredient.ingredientId where fridge.ingredientId = ? and fridge.userId = ?";
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
   * Deletes an ingredient from the fridge.
   *
   * @param ingredientId The id of the ingredient to delete.
   */
  public void deleteFromDatabase(int ingredientId, int unitId) {
    try {
      String query = "DELETE From fridge where userId = ? and ingredientId = ? and unitId = ?";
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
   * Empties the fridge.
   */
  public void emptyFridge() {
    try {
      String query = "DELETE From fridge where userId = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(query);
      statement.setInt(1, Auth.currentUser().getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e);
    }
  }

  /**
   * Gets the unit for an ingredient in the fridge.
   *
   * @param ingredientId The id of the ingredient to get the unit for.
   * @return The unit name for that ingredient.
   */
  public ArrayList<String> getUnitForIngredient(int ingredientId) {

    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT unitName from fridge inner join unit on fridge.unitId"
              + " = unit.unitId where fridge.ingredientId ="
              + ingredientId + "and fridge.ingredientId =" + Auth.currentUser().getId());
      ResultSet resultSet = statement.executeQuery();
      ArrayList<String> unitName = new ArrayList<>();
      while (resultSet.next()) {
        unitName.add(resultSet.getString("unitId"));
      }
      return unitName;

    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  /**
   * Updates the ingredient in the fridge with new values.
   *
   * @param ingredient The ingredient with the new values.
   */
  public void updateIngredientInFridge(TripleTuple<cookbook.Ingredient, Double, Unit> ingredient,
       Unit oldUnit) {
    try {
      // Create the UPDATE SQL query
      String query = "UPDATE fridge SET quantity = ?, unitId = ? WHERE "
          + "ingredientId = ? and userId = ? and unitId = ?";
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

  /**
   * Insert a new ingredient into the fridge.
   *
   * @param newIngredient true if it is a new ingredient.
   */
  public void inputFridgeIngredient(TripleTuple<cookbook.Ingredient, Double, Unit> ingredientTuple,
      boolean newIngredient) {
    // Add new ingredient to database.
    if (newIngredient) {
      try {
        DatabaseStatement databaseStatement = new DatabaseStatement();
        IngredientDao ingredientDao = new IngredientDao();
        databaseStatement.insert("ingredient",
            new String[] { "ingredientName" },
            new String[] { ingredientTuple.first.getName() },
            false);
        int id = ingredientDao.getIngredientId(ingredientTuple.first.getName());
        databaseStatement.insert("fridge",
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
    ArrayList<TripleTuple<cookbook.Ingredient, Double, Unit>> ingredientItems = 
        getIngredientInFridge(ingredientTuple.first.getId());
    TripleTuple<cookbook.Ingredient, Double, Unit> originIngredient = null;
    boolean isConverted = false;
    Double convertedQuantity = null;
    IngredientDao ingredientDao = new IngredientDao();
    if (ingredientItems.size() > 0) {
      for (int i = 0; i < ingredientItems.size(); i++) {
        try {
          convertedQuantity = searchInFile.convertToUnit(
              ingredientTuple.first.getName(), ingredientTuple.second,
              ingredientTuple.third, ingredientItems.get(
                  i).third,
              ingredientDao
                  .getDensity(ingredientTuple.first.getId()));
        } catch (IllegalArgumentException e) {
          originIngredient = ingredientItems.get(i);
        }
        if (convertedQuantity != null) {
          originIngredient = ingredientItems.get(i);
          isConverted = true;
          break;
        }
      }
      TripleTuple<cookbook.Ingredient, Double, Unit> insertTuple;
      if (convertedQuantity == null) {
        insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
            ingredientTuple.first.getId(), ingredientTuple.first.getName(),
             ingredientTuple.first.getDensity()),
            ingredientTuple.second, new Unit(ingredientTuple.third.getId(),
             ingredientTuple.third.getName()));
        try {
          DatabaseStatement databaseStatement = new DatabaseStatement();
          databaseStatement.insert("fridge",
              new String[] { "userId", "ingredientId", "quantity", "unitId" },
              new String[] { Integer.toString(Auth.currentUser().getId()),
                  Integer.toString(insertTuple.first.getId()),
                  Double.toString(insertTuple.second), Integer
                  .toString(insertTuple.third.getId()) },
              false);
        } catch (Exception e) {
          System.err.println(e);
        }
      } else {
        if (isConverted && originIngredient != null) {
          //System.err.println("It was converted and inserted");
          insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
              ingredientTuple.first.getId(), ingredientTuple.first.getName(),
               ingredientTuple.first.getDensity()),
              convertedQuantity + originIngredient.second,
              new Unit(originIngredient.third.getId(), originIngredient.third.getName()));
          updateIngredientInFridge(insertTuple, originIngredient.third);
        } else if (originIngredient != null) {
          //System.err.println("No need for conversion");
          insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
              ingredientTuple.first.getId(), ingredientTuple.first.getName(), 
              ingredientTuple.first.getDensity()),
              ingredientTuple.second + originIngredient.second,
              new Unit(originIngredient.third.getId(), originIngredient.third.getName()));
          updateIngredientInFridge(insertTuple, ingredientTuple.third);
        } else {
          System.err.println("The originIngredient was not set");
        }
      }
    } else {
      //System.err.println("New ingredient added to fridge");
      TripleTuple<cookbook.Ingredient, Double, Unit> insertTuple;
      insertTuple = new TripleTuple<cookbook.Ingredient, Double, Unit>(new cookbook.Ingredient(
          ingredientTuple.first.getId(), ingredientTuple.first.getName(),
           ingredientTuple.first.getDensity()),
          ingredientTuple.second,
          new Unit(ingredientTuple.third.getId(), ingredientTuple.third.getName()));
      try {
        DatabaseStatement databaseStatement = new DatabaseStatement();
        databaseStatement.insert("fridge",
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

}
