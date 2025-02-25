package cookbook;

import cookbook.db.DB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;


/** It is the class in order to search for an Ingredient.
 * 
 */
public class SearchIngredient extends Application {
  private TextField ingName;
  private HashMap<Integer, ArrayList<Integer>> recipeIds = new HashMap<>();
  private boolean rowsAffected;
  private Stage stage;
  private CustomAlertMessage customAlertMessage;
  private Image iImage;
  private ImageHandler im;

  public SearchIngredient() {

  }
  /** Is the method in order to find all ingredients ID.

   * @param ingredient is the given ingredient.
   * @return returns ingredient Ids.
   */
  public ArrayList<Integer> searcH(String ingredient) {
    ArrayList<Integer> ingredients = new ArrayList<>();
    try {
      String query = "SELECT ci.ingredientId FROM cookbook.ingredient AS";
      query += " ci WHERE ci.ingredientName LIKE (?)";
      try (PreparedStatement fetch = DB.getInstance().getConnection().prepareStatement(query)) {
        fetch.setString(1, ingredient + "%");
        try (ResultSet resultSet = fetch.executeQuery()) {
          while (resultSet.next()) {
            int ingId = resultSet.getInt(1);
            ingredients.add(ingId);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ingredients;
  }

  /** Is the method on order to return the recipeIds.

   * @param ingredients is the ingredientsID.
   * @return it returns the recipes.
   */
  public ArrayList<String> searchForRecipeId(ArrayList<Integer> ingredients) {
    ArrayList<String> recipes = new ArrayList<String>();
    try {
      String query = "SELECT cr.* FROM cookbook.recipe AS cr JOIN cookbook.";
      query += "recipe_ingredient AS cri ON cr.recipeId = cri.recipeId WHERE";
      query += " cri.ingredientId = (?) ";
      try (PreparedStatement query2 = DB.getInstance().getConnection().prepareStatement(query)) {
        for (int ingId : ingredients) {
          query2.setInt(1, ingId);
          try (ResultSet recipe = query2.executeQuery()) {
            while (recipe.next()) {
              int recId = recipe.getInt(1);
              getImageId("images", recId);
              int userId = recipe.getInt(2);
              String recName = recipe.getString(3);
              String recSh = recipe.getString(4);
              String recLo = recipe.getString(5);
              String recPo = recipe.getString(6);
              String all =  "recipe name: " + recName;
              all += "Short description: ";
              all += recSh + "Long description: " + recLo + ", Portion" + recPo;
              recipes.add(all);
            }
          }
        }
      }
      this.rowsAffected = (recipes.size() > 0);
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }
    return recipes;
  }

  /**
   * A method in order to convert a String into an array of characters.

   * @param x is the string.
   * @return returns the array of characters.
   */
  public char [] stringConverter(String x) {
    int length = x.length();
    char [] chars = new char[length];
    for (int i = 0; i < length; i++) {
      chars[i] = x.charAt(i);
    }
    return chars;
  }

  /**
   * Is a method to check whether all the elements of an array are true or there are some false.

   * @param x is the given array.
   * @return returns a boolean.
   */
  public boolean allTrue(boolean [] x) {
    for (boolean t : x) {
      if (t != true) {
        return false;
      }
    }
    return true;
  }

  /** We have this method in order to retrive the imageId.

   * @param table is the intended image table.
   * @param recipeId is the given recipeId.
   */
  public void getImageId(String table, int recipeId) {
    ArrayList<Integer> ids = new ArrayList<>();
    String q = "SELECT * FROM cookbook." + table + "WHERE recipeId = (?)";
    try (PreparedStatement st = DB.getInstance().getConnection().prepareStatement(q)) {
      st.setInt(1, recipeId);
      try (ResultSet rs = st.executeQuery()) {
        if (! rs.next()) {
          throw new IllegalArgumentException("The given recipe does not have any image.");
        }
        while (rs.next()) {
          int id = rs.getInt(1);
          ids.add(id);
        }
      }
      recipeIds.put(recipeId, ids);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException ee) {
      System.err.println(ee);
    }
  }

  public void getImage(int imageId) {
    try {
      this.iImage = im.displayImage("images", imageId);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  /** It's here just to test.

   * @param x is or are the ingredients.
   * @return it returns all the ingredient.
   */
  public ArrayList<String> tester(String x) {
    ArrayList<Integer> ids = searcH(x);
    ArrayList<String> rec = searchForRecipeId(ids);
    return rec;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.stage = primaryStage;
    this.customAlertMessage = new CustomAlertMessage();
    final Label ingredient = new Label("Please write the intended ingredients");
    TextField inputIng = new TextField();
    inputIng.setPromptText("Separate each ingredient by a comma (,)");
    this.ingName = inputIng;
    inputIng.setStyle("-fx-background-radius: 5;");


    Button search = new Button("Search");
    search.setOnAction(event -> {
      try {
        starter();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Button resetButton = new Button("Back");
    resetButton.setOnAction(event -> {
      // Here must be implemented.
      System.out.println("Hello");
    });
    String reset = "-fx-background-radius: 50; -fx-font-size: 18px;";
    reset += " -fx-padding: 5px;";
    resetButton.setStyle(reset);
    resetButton.setText("Return");

    search.setStyle("-fx-background-radius: 30;");

    VBox root = new VBox(20);
    root.setPadding(new Insets(20));
    root.getChildren().addAll(ingredient, inputIng, search, resetButton);

    Scene scene = new Scene(root, 800, 500);
    stage.setScene(scene);
    stage.setTitle("Search for a new recipe by ingredients");
    stage.show();

  }
  /** This method is here for to check if the box is not empty or blank.

   * @param in is the input.
   * @return returns either true or false.
   */
  public boolean check(TextField in) {
    String ingredient = in.getText().trim();
    return !ingredient.isEmpty() && !ingredient.isBlank();
  }

  /** This is a method in order to check if the inputTag is written correctly.
   * * @param ing are ingredients as a TextField.

   * @return returns either true or false.
   */
  public boolean checkForIng(TextField ing) {
    String input = ing.getText().trim();
    String[] parts = input.split("\\s*,\\s*");
    for (String part : parts) {
      if (part.isEmpty() || !part.matches("[a-zA-Z\\s]+")) {
        return false;
      }
    }
    return true; // All parts are valid
  }

  /** The starter method which is used in search button.
   * 
   */
  public void starter() {

    VBox vbox = new VBox(20);
    vbox.setPadding(new Insets(20));
    try {
      if (!check(ingName)) {
        throw new IllegalArgumentException("Ingredients input can't be empty!");
      }
      if (!checkForIng(ingName)) {
        throw new IllegalArgumentException("The given input is not in right format!");
      }
  
      String ingredient = ingName.getText();
      final String[] ingredients = ingredient.split("\\s*,\\s*");
      GridPane root = new GridPane();
      root.setPadding(new Insets(20));
      root.setHgap(35);
      root.setVgap(35);
      int rowIndex = 0;
      if (ingredients.length == 0) {
        throw new IllegalArgumentException("No Input !!!");
      }

      System.out.println(ingredients.length);
      for (int i = 0; i < ingredients.length; i++) {
        ArrayList<Integer> ids = searcH(ingredients[i]);
        ArrayList<String> recipes = searchForRecipeId(ids);
        if (recipes.size() == 0) {
          throw new IllegalArgumentException("Nothing was found!!!");
        }
        Label ingType = new Label(ingredients[i]);
        ingType.setStyle("-fx-font-weight: bold;");
        root.add(ingType, 0, rowIndex++);
        for (int j = 0; j < recipes.size(); j++) {
          Label r = new Label(recipes.get(j));
          root.add(r, 0, rowIndex++);
        }

      }

      Scene scene = new Scene(root, 800, 500);
      stage.setScene(scene);
      stage.setTitle("The app message.");
      stage.show();
    } catch (IllegalArgumentException e) {
      displayErrorAlert(e.getMessage());
    } catch (Exception ee) {
      displayErrorAlert(ee.getMessage());
    }
  }
  

  private void displayErrorAlert(String errorMessage) {
    StackPane root = new StackPane();
    String image = "/479B9BC0-5C88-431D-8F4F-2C6AACFE0828_4_5005_c.jpeg";
    Image background = new Image(getClass().getResource(image).toExternalForm());
    ImageView backImageView = new ImageView(background);
    root.getChildren().add(backImageView);

    // Create the custom alert using the CustomAlertMessage class
    Alert alert = customAlertMessage.createCustomAlert(errorMessage);

    root.getChildren().add(alert.getDialogPane().getContent());

    Scene scene = new Scene(root, 400, 200);
    Stage alertStage = new Stage();
    alertStage.setScene(scene);
    alertStage.setTitle("Error");
    alertStage.show();

    // Close the alert stage after 3 seconds
    PauseTransition delay = new PauseTransition(Duration.seconds(3));
    delay.setOnFinished(event -> alertStage.close());
    delay.play();
  }

}

