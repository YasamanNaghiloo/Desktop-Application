package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Ingredient {
  protected int ingredientId;
  protected String ingredientName;
  protected double density;

  protected Ingredient() {
  }

  public int getId() {
    return this.ingredientId;
  }

  public String getName() {
    return this.ingredientName;
  }

  public static Ingredient fromDB(int id) {
    String selectSql = "SELECT * FROM ingredient WHERE ingredientId = ?";
    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, id);
      try (ResultSet resultSet = stmt.executeQuery()) {
        if (resultSet.next()) {
          return fromResultSet(resultSet);
        }
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  public static Ingredient fromDB(String name) {
    String selectSql = "SELECT * FROM ingredient WHERE ingredientName = ?";
    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      stmt.setString(1, name);
      try (ResultSet resultSet = stmt.executeQuery()) {
        if (resultSet.next()) {
          return fromResultSet(resultSet);
        }
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  public static ArrayList<Ingredient> getAll() {
    ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    String selectSql = "SELECT * FROM ingredient";
    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql);
        ResultSet resultSet = stmt.executeQuery()) {
      while (resultSet.next()) {
        ingredients.add(fromResultSet(resultSet));
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredients;
  }

  public static ArrayList<Ingredient> getAllLike(String like) {
    ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    String selectSql = "SELECT * FROM ingredient WHERE ingredientName LIKE ?";
    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      stmt.setString(1, "%" + like + "%");
      try (ResultSet resultSet = stmt.executeQuery()) {
        while (resultSet.next()) {
          ingredients.add(fromResultSet(resultSet));
        }
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredients;
  }

  public static Ingredient fromResultSet(ResultSet resultSet) throws SQLException {
    if (resultSet == null) {
      Ingredient ingredient = new Ingredient();

      ingredient.ingredientId = -1;
      ingredient.ingredientName = "";
      ingredient.density = -1;
      
      return ingredient;
    }
    Ingredient ingredient = new Ingredient();

    ingredient.ingredientId = resultSet.getInt("ingredientId");
    ingredient.ingredientName = resultSet.getString("ingredientName");
    ingredient.density = resultSet.getDouble("density");

    return ingredient;
  }

  public static void addIngredient(int ingredientId, double quantity, int unitId, int recipeId) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection()
          .prepareStatement("INSERT INTO recipe_ingredient VALUES (?, ?, ?, ?);");
      stmt.setInt(1, ingredientId);
      stmt.setInt(2, recipeId);
      stmt.setInt(3, unitId);
      stmt.setDouble(4, quantity);
      stmt.executeUpdate();
      System.out.println("Ingredient added to recipe");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void addIngredientToDatabase(String name) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(
          "INSERT INTO ingredient VALUES (Default, ?, Default) ON DUPLICATE KEY UPDATE ingredientName = ingredientName;");
      stmt.setString(1, name);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void addIngredientToDatabase(String name, double density) {
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(
          "INSERT INTO ingredient VALUES (Default, ?, ?) ON DUPLICATE KEY UPDATE ingredientName = ingredientName;");
      stmt.setString(1, name);
      stmt.setDouble(2, density);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return getName();
  }
}
