package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



                              // NOT DONE YET!!!!!
public class RecipeIngredient extends Ingredient {

  private int recipeId;
  private int portion;
  private Unit unit;

  private transient static final DB db = DB.getInstance();

  private RecipeIngredient() {}

  public Unit getIngredientUnit() {
    return this.unit;
  }

  public static RecipeIngredient fromRecipeResultSet(ResultSet resultSet) throws SQLException {
    if (resultSet == null) {
      return null;
    }
    RecipeIngredient ingredient = new RecipeIngredient();
    
    ingredient.recipeId = resultSet.getInt("recipeId");
    ingredient.ingredientId = resultSet.getInt("ingredientId");
    ingredient.ingredientName = resultSet.getString("ingredientName");
    
    Unit unit = new Unit();
    unit.unitName = resultSet.getString("unitName");

    ingredient.portion = resultSet.getInt("quantity");

    ingredient.unit = unit;
    
    return ingredient;
  }
  
  public static ArrayList<RecipeIngredient> fromRecipe(int recipeId) {
    ArrayList<RecipeIngredient> ingredients = new ArrayList<RecipeIngredient>();
    String selectSql = "SELECT ri.recipeId, ri.ingredientId, i.ingredientName, u.unitName, ri.quantity";
    selectSql += " FROM recipe_ingredient ri JOIN ingredient i ON ri.ingredientId = i.ingredientId";
    selectSql += " JOIN unit u ON ri.unitId = u.unitId WHERE ri.recipeId = ? ";
    selectSql += "ORDER BY ri.recipeId, i.ingredientName";

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, recipeId);
      try (ResultSet resultSet = stmt.executeQuery()) {
        while (resultSet.next()) {
          ingredients.add(fromRecipeResultSet(resultSet));
        }
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ingredients;
  }

  @Override
  public String toString() {
    return this.ingredientName + " " + this.portion + " " + this.unit.getUnitName();
  }
  
  static public class Unit  {
    
    private String unitName;
    private int unitId;
    private String type;

    private Unit() {}


    public String getUnitName() {
      return this.unitName;
    }

    public int getUnitId() {
      return unitId;
    }

    public String getType() {
      return type;
    }

    public static ArrayList<Unit> getAll() {
      String selectSql = "SELECT * FROM unit"; // Using PreparedStatement to prevent SQL injection

      try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
        ResultSet resultSet = stmt.executeQuery();
        ArrayList<Unit> units = new ArrayList<Unit>();

        while (resultSet.next()) {
          units.add(fromResultSet(resultSet));
        }
        return units;
      } catch (SQLException e) {
        System.out.println("An error has occurred: " + e.getMessage());
        return null;
      }
    }

    public static Unit fromDB(String name) {
      try {
        PreparedStatement stmt = db.getConnection().prepareStatement("SELECT * FROM unit WHERE unitName = ?;");
        stmt.setString(1, name);
        ResultSet result = stmt.executeQuery();
        result.next();
        return fromResultSet(result);
      } catch (SQLException e) {
        e.printStackTrace();
        return null;
      }
    }

    public static Unit fromResultSet(ResultSet resultSet) throws SQLException {
      Unit unit = new Unit();

      unit.unitId = resultSet.getInt(1);
      unit.unitName = resultSet.getString(2);
      unit.type = resultSet.getString(3);
      
      return unit;
    }
  }
}
