package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import cookbook.RecipeInterface;
import cookbook.auth.Auth;

public class Recipe implements RecipeInterface{
  // `recipeId` int NOT NULL AUTO_INCREMENT,
  // `userId` int NOT NULL,
  // `recipeName` varchar(45) NOT NULL,
  // `shortDescription` varchar(50) NOT NULL,
  // `fullDescription` varchar(300) NOT NULL,
  // `portion` int NOT NULL,
  private int id;
  private int userId;
  private String name;
  private String shortDescription;
  private String fullDescription;
  private String imageUrl;
  private int portion;

  private ArrayList<RecipeIngredient> ingredients;
  private ArrayList<Tag> tags;

  // If we ever want to serialize this class, we need to make sure that the DB is not serialized
  private transient static final DB db = DB.getInstance();

  private Recipe() {
  }

  public static Recipe fromDB(int id) {
    String selectSql = "SELECT * FROM recipe WHERE recipeId = ? LIMIT 1"; // Using PreparedStatement to prevent SQL
                                                                          // injection

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

  public static Recipe fromDB(String recipeName) {
    String selectSql = "SELECT * FROM recipe WHERE recipeName = ? LIMIT 1"; // Using PreparedStatement to prevent SQL
                                                                          // injection

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setString(1, recipeName);
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

  public static ArrayList<Recipe> getAll() {
    String selectSql = "SELECT * FROM recipe"; // Using PreparedStatement to prevent SQL injection

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<Recipe>();

      while (resultSet.next()) {
        recipes.add(fromResultSet(resultSet));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  public static ArrayList<Recipe> getAllLike(String like) {
    String selectSql = "SELECT * FROM recipe WHERE recipeName LIKE ?";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setString(1, "%" + like + "%");
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<Recipe>();

      while (resultSet.next()) {
        recipes.add(fromResultSet(resultSet));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  public static ArrayList<Recipe> getAllFromUser(int userId) {
    String selectSql = "SELECT * FROM recipe WHERE userId = ?"; // Using PreparedStatement to prevent SQL injection

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, userId);
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<Recipe>();

      while (resultSet.next()) {
        recipes.add(fromResultSet(resultSet));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Get all recipes from a user that contains a certain string in the recipe name
   * 
   * @param userId int
   * @param like   String
   * 
   * @return ArrayList<Recipe> the recipes that match the query
   */
  public static ArrayList<Recipe> getAllFromUserLike(int userId, String like) {
    String selectSql = "SELECT * FROM recipe WHERE userId = ? AND recipeName LIKE ?"; // Using PreparedStatement to
                                                                                      // prevent SQL injection

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, userId);
      stmt.setString(2, "%" + like + "%");
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<Recipe>();

      while (resultSet.next()) {
        recipes.add(fromResultSet(resultSet));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Get all recipes that a user has favorited
   * 
   * @param userId int
   * 
   * @return ArrayList<Recipe> the recipes that the user has favorited
   */
  public static ArrayList<Recipe> getAllFavorites(int userId) {
    String selectSql = "SELECT recipeId FROM favorite WHERE userId = ?;";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, userId);
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<Recipe>();

      while (resultSet.next()) {
        recipes.add(fromDB(resultSet.getInt("recipeId")));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  public static ArrayList<Recipe> getAllFavoritesLike(int userId, String like) {
    String selectSql = "SELECT recipeId FROM favorite WHERE userId = ? " +
                       "AND recipeId IN (SELECT recipeId FROM recipe WHERE recipeName LIKE ?)";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql)) {
      stmt.setInt(1, userId);
      stmt.setString(2, "%" + like + "%");
      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<Recipe>();

      while (resultSet.next()) {
        recipes.add(fromDB(resultSet.getInt("recipeId")));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Search for recipes based on the provided parameters
   * 
   * @param recipeNameLike The recipe name to search for
   * @param tagIds         The tags the recipe should have
   * @param ingredientIds  The ingredients the recipe should have
   * @return The recipes that match the query
   */
  public static ArrayList<Recipe> searchAll(String recipeNameLike, ArrayList<Integer> tagIds,
      ArrayList<Integer> ingredientIds) {
    // Start with a true condition do we don't need to check later if this is the
    // first condition
    // because if it's the first we have to use WHERE instead of AND
    StringBuilder selectSql = new StringBuilder("SELECT * FROM recipe WHERE 1=1");
    ArrayList<Object> params = new ArrayList<>();

    // Add conditions for recipeName if provided
    if (recipeNameLike != null && !recipeNameLike.isEmpty()) {
      selectSql.append(" AND recipeName LIKE ?");
      params.add("%" + recipeNameLike + "%");
    }

    // Add conditions for tagIds if provided
    if (tagIds != null && !tagIds.isEmpty()) {
      selectSql.append(" AND recipeId IN (SELECT recipeId FROM recipe_tag WHERE tagId IN (");
      selectSql.append(String.join(",", Collections.nCopies(tagIds.size(), "?")));
      selectSql.append(") GROUP BY recipeId HAVING COUNT(DISTINCT tagId) = ?)");
      params.addAll(tagIds);
      params.add(tagIds.size()); // Number of tags must match
    }

    // Add conditions for ingredientIds if provided
    if (ingredientIds != null && !ingredientIds.isEmpty()) {
      int numIngredients = ingredientIds.size();
      selectSql.append(" AND recipeId IN (SELECT recipeId FROM recipe_ingredient WHERE ingredientId IN (");
      selectSql.append(String.join(",", Collections.nCopies(numIngredients, "?")));
      selectSql.append(") GROUP BY recipeId HAVING COUNT(DISTINCT ingredientId) = ?)");
      params.addAll(ingredientIds);
      params.add(numIngredients); // Number of ingredients must match
    }

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql.toString())) {
      // Set parameters
      int index = 1;
      for (Object param : params) {
        stmt.setObject(index++, param);
      }

      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<>();

      while (resultSet.next()) {
        recipes.add(fromResultSet(resultSet));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Get all recipes from a user which match the provided parameters
   * 
   * @param userId         the id of the user
   * @param recipeNameLike the name of the recipe
   * @param tagIds         the tags the recipe should have
   * @param ingredientIds  the ingredients the recipe should have
   * @return the recipes that match the query
   */
  public static ArrayList<Recipe> searchAllFromUser(int userId, String recipeNameLike, ArrayList<Integer> tagIds,
      ArrayList<Integer> ingredientIds) {
    ArrayList<Object> params = new ArrayList<>();
    // Start with a true condition do we don't need to check later if this is the
    // first condition
    // because if it's the first we have to use WHERE instead of AND
    StringBuilder selectSql = new StringBuilder("SELECT * FROM recipe WHERE userId = ?");
    params.add(userId);

    // Add conditions for recipeName if provided
    if (recipeNameLike != null && !recipeNameLike.isEmpty()) {
      selectSql.append(" AND recipeName LIKE ?");
      params.add("%" + recipeNameLike + "%");
    }

    // Add conditions for tagIds if provided
    if (tagIds != null && !tagIds.isEmpty()) {
      selectSql.append(" AND recipeId IN (SELECT recipeId FROM recipe_tag WHERE tagId IN (");
      selectSql.append(String.join(",", Collections.nCopies(tagIds.size(), "?")));
      selectSql.append(") GROUP BY recipeId HAVING COUNT(DISTINCT tagId) = ?)");
      params.addAll(tagIds);
      params.add(tagIds.size()); // Number of tags must match
    }

    // Add conditions for ingredientIds if provided
    if (ingredientIds != null && !ingredientIds.isEmpty()) {
      int numIngredients = ingredientIds.size();
      selectSql.append(" AND recipeId IN (SELECT recipeId FROM recipe_ingredient WHERE ingredientId IN (");
      selectSql.append(String.join(",", Collections.nCopies(numIngredients, "?")));
      selectSql.append(") GROUP BY recipeId HAVING COUNT(DISTINCT ingredientId) = ?)");
      params.addAll(ingredientIds);
      params.add(numIngredients); // Number of ingredients must match
    }

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql.toString())) {
      // Set parameters
      int index = 1;
      for (Object param : params) {
        stmt.setObject(index++, param);
      }

      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<>();

      while (resultSet.next()) {
        recipes.add(fromResultSet(resultSet));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Get all recipes that a user has favorited which match the provided parameters
   * 
   * @param userId         the id of the user
   * @param recipeNameLike the name of the recipe
   * @param tagIds         the tags the recipe should have
   * @param ingredientIds  the ingredients the recipe should have
   * @return the recipes that match the query
   */
  public static ArrayList<Recipe> searchAllFavorites(int userId, String recipeNameLike, ArrayList<Integer> tagIds,
      ArrayList<Integer> ingredientIds) {
    ArrayList<Object> params = new ArrayList<>();
    // Start with a true condition do we don't need to check later if this is the
    // first condition
    // because if it's the first we have to use WHERE instead of AND
    StringBuilder selectSql = new StringBuilder("SELECT * FROM recipe WHERE userId = ?");
    params.add(userId);

    // Add conditions for recipeName if provided
    if (recipeNameLike != null && !recipeNameLike.isEmpty()) {
      selectSql.append(" AND recipeName LIKE ?");
      params.add("%" + recipeNameLike + "%");
    }

    // Add conditions for tagIds if provided
    if (tagIds != null && !tagIds.isEmpty()) {
      selectSql.append(" AND recipeId IN (SELECT recipeId FROM recipe_tag WHERE tagId IN (");
      selectSql.append(String.join(",", Collections.nCopies(tagIds.size(), "?")));
      selectSql.append(") GROUP BY recipeId HAVING COUNT(DISTINCT tagId) = ?)");
      params.addAll(tagIds);
      params.add(tagIds.size()); // Number of tags must match
    }

    // Add conditions for ingredientIds if provided
    if (ingredientIds != null && !ingredientIds.isEmpty()) {
      int numIngredients = ingredientIds.size();
      selectSql.append(" AND recipeId IN (SELECT recipeId FROM recipe_ingredient WHERE ingredientId IN (");
      selectSql.append(String.join(",", Collections.nCopies(numIngredients, "?")));
      selectSql.append(") GROUP BY recipeId HAVING COUNT(DISTINCT ingredientId) = ?)");
      params.addAll(ingredientIds);
      params.add(numIngredients); // Number of ingredients must match
    }

    selectSql.append(" AND recipeId IN (SELECT recipeId FROM favorite WHERE userId = ?)");
    params.add(userId);

    try (PreparedStatement stmt = db.getConnection().prepareStatement(selectSql.toString())) {
      // Set parameters
      int index = 1;
      for (Object param : params) {
        stmt.setObject(index++, param);
      }

      ResultSet resultSet = stmt.executeQuery();
      ArrayList<Recipe> recipes = new ArrayList<>();

      while (resultSet.next()) {
        recipes.add(fromResultSet(resultSet));
      }
      return recipes;
    } catch (SQLException e) {
      System.out.println("An error has occurred: " + e.getMessage());
      return null;
    }
  }

  /**
   * Recipe Factory Method to load the values from a ResultSet
   * 
   * @param resultSet ResultSet
   * 
   * @return Recipe object
   */
  public static Recipe fromResultSet(ResultSet resultSet) throws SQLException {
    Recipe recipe = new Recipe();

    recipe.id = resultSet.getInt("recipeID");
    recipe.userId = resultSet.getInt("userId");
    recipe.name = resultSet.getString("recipeName");
    recipe.shortDescription = resultSet.getString("shortDescription");
    recipe.fullDescription = resultSet.getString("fullDescription");
    recipe.portion = resultSet.getInt("portion");
    recipe.imageUrl = resultSet.getString("imageUrl");

    recipe.ingredients = RecipeIngredient.fromRecipe(recipe.id);
    recipe.tags = Tag.fromRecipe(recipe.id);

    return recipe;
  }

  /**
   * ----------------- "CRUD" OPERATIONS -----------------
   */

  /**
   * Add a comment to the recipe
   * @param recipeId The ID of the recipe
   * @param content The content of the comment
   * @return The ID of the new comment, or -1 if the operation failed
  */
  public int addComment(String content) throws SQLException {
    String insertSql = "INSERT INTO comment VALUES (Default, ?, ?, ?, Default)";
    
    try (PreparedStatement stmt = db.getConnection().prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
      stmt.setInt(1, this.id);
      stmt.setInt(2, Auth.currentUser().getId());
      stmt.setString(3, content);
      stmt.executeUpdate();
      ResultSet resultSet = stmt.getGeneratedKeys();
      if (resultSet.next()) {
        return resultSet.getInt(1);
      } else {
        throw new SQLException("No ID returned from the database after inserting a new comment");
      }
    }
  }

  /**
   * ----------------- GETTERS -----------------
   */

  public int getId() {
    return id;
  }

  public int getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public String getImagePath() {
    return imageUrl;
  }

  public int getPortion() {
    return portion;
  }

  public ArrayList<RecipeIngredient> getIngredients() {
    return ingredients;
  }

  public ArrayList<Tag> getTags() {
    return tags;
  }

  @Override
  public String toString() {
    return name;
  }

}