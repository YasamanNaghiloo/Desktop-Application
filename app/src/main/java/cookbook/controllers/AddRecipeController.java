package cookbook.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import cookbook.auth.Auth;
import cookbook.db.DB;
import cookbook.db.Ingredient;
import cookbook.navigator.Route;
import cookbook.server.Server;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

public class AddRecipeController extends RouteController implements Initializable {
  private ObservableList<Ingredient> ingredients;

  @FXML
  private Spinner<Double> amount;

  @FXML
  private TextArea displayIngredients;

  @FXML
  private ComboBox<Ingredient> ingredientName;

  @FXML
  private ComboBox<String> unit;

  @FXML
  private TextArea instructions;

  @FXML
  private TextField portion;

  @FXML
  private ImageView recipeImage;

  @FXML
  private TextField recipeName;

  @FXML
  private TextField shortDescription;

  @FXML
  private TextField tags;

  @FXML
  private ComboBox<String> predefinedTags;

  private String[] arrayOfTags = { "Carnivore", "Omnivore", "Vegetarian", "Vegan", "Lactose Free",
      "Gluten Free", "Starter", "Main Course", "Dessert", "Sweet" };

  // new comment
  private File selectedImage;

  @FXML
  public void tagChoice(ActionEvent event) {
    System.out.println("Tag chosen: " + predefinedTags.getSelectionModel().getSelectedItem());
    String chosenTag = predefinedTags.getSelectionModel().getSelectedItem();
    tags.appendText(chosenTag + ", ");
  }

  @FXML
  private void addIngredient(ActionEvent event) {
    if (ingredientName.getEditor().getText().equals("") || amount.getValue() == 0
        || unit.getSelectionModel().getSelectedItem() == null) {
      getNavigator().showDialog("Invalid Ingredient", "Please enter a valid form of ingredient");
    } else {
      displayIngredients.appendText(ingredientName.getEditor().getText() + ", " + amount.getValue() + ", "
          + unit.getSelectionModel().getSelectedItem() + "\n");
      ingredientName.getEditor().setText("");
    }
  }

  private boolean validate() {
    if (recipeName.getText().isEmpty() ||
        shortDescription.getText().isEmpty() ||
        displayIngredients.getText().isEmpty() ||
        instructions.getText().isEmpty() ||
        portion.getText().isEmpty() ||
        tags.getText().isEmpty()) {
      return false;
    }
    System.out.println("Im ok! validate");
    return true;
  }

  /**
   * Validate the ingredients field
   * 
   * @return true if the ingredients are valid, false otherwise
   */
  public boolean validateIngredients() {
    String[] ingredients = displayIngredients.getText().trim().split("\n");
    for (String ingredient : ingredients) {
      String[] part = ingredient.split(" ");
      try {
        String query = "SELECT * FROM ingredient WHERE ingredientName = ?;";
        PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(query);
        stmt.setString(1, part[0]);
        ResultSet result = stmt.executeQuery();
        if (result == null) {
          return false;
        }
      } catch (SQLException e) {
        showError(e);
        return false;
      }
    }
    return true;
  }

  private String uploadAnImage() {
    try {
      String imageUrl = Server.uploadFile(selectedImage);
      return imageUrl;
    } catch (IOException e) {
      getNavigator().showDialog("Error", e.getMessage());
    }
    return "";
  }

  private void insertAnImage(String imageUrl, int recipeId, int userId) {
    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(
        "UPDATE cookbook.recipe SET imageURL = ? WHERE recipeId = ? AND userId = ? ")) {
      stmt.setString(1, imageUrl);
      stmt.setInt(2, recipeId);
      stmt.setInt(3, Auth.currentUser().getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      getNavigator().showDialog("Error", "SQL Error: " + e.getMessage());
    }
  }

  @FXML
  private void btSubmit(ActionEvent event) {
    if (!validate()) {
      getNavigator().showDialog("Error", "Please fill in all fields");
      return;
    }

    int recipeId = -1;
    /**
     * Insert the recipe into the database
     */
    String q = "INSERT INTO cookbook.recipe (userId, recipeName, shortDescription, fullDescription, portion) VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(q,
        Statement.RETURN_GENERATED_KEYS)) {
      stmt.setInt(1, Auth.currentUser().getId());
      stmt.setString(2, recipeName.getText());
      stmt.setString(3, shortDescription.getText());
      stmt.setString(4, instructions.getText());
      stmt.setInt(5, Integer.parseInt(portion.getText()));

      stmt.executeUpdate();
      System.out.println("Recipe insert executed");
      try (ResultSet genRecipeId = stmt.getGeneratedKeys()) {
        if (!genRecipeId.next()) {
          throw new Exception("Failed to get recipeId from database.");
        }
        recipeId = genRecipeId.getInt(1);
      }

      // Handle image upload if selectedImage is not null
      if (selectedImage != null) {
        String url = uploadAnImage();
        if (!url.isEmpty()) {
          insertAnImage(url, recipeId, Auth.currentUser().getId());
        } else {
          System.out.println("Note: No image was uploaded.");
        }
      }
    } catch (SQLException e) {
      getNavigator().showDialog("Error", "SQL Error: " + e.getMessage());
      e.printStackTrace();
      return;
    } catch (Exception e) {
      getNavigator().showDialog("Error", e.getMessage());
      e.printStackTrace();
      return;
    }

    // Check if the recipe was added to the database before adding tags etc
    if (recipeId == -1) {
      getNavigator().showDialog("Error", "Failed to add recipe to database.");
      return;
    }

    /**
     * Add the tags to the database if they don't exist and add them to the recipe
     */
    String[] tagArray = tags.getText().split(",");
    System.out.println("Tags: " + Arrays.toString(tagArray));
    for (String tag : tagArray) {
      if (tag.isEmpty()) {
        continue;
      }
      int currentTagId = -1;
      String q2 = "INSERT INTO cookbook.tag (tagName) VALUES (?) ON DUPLICATE KEY UPDATE tagName=tagName";

      try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(q2,
          Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, tag);
        stmt.executeUpdate();
        try (ResultSet genTagId = stmt.getGeneratedKeys()) {
          if (genTagId.next()) {
            currentTagId = genTagId.getInt(1);
          } else {
            // tag already exists
            currentTagId = getTagIdFromName(tag);
          }
          System.out.println("Tag ID: " + currentTagId + " for tag: " + tag);
        }
      } catch (SQLException e) {
        getNavigator().showDialog("Error", "Failed to add tag: " + tag);
        e.printStackTrace();
        continue;
      } catch (Exception e) {
        getNavigator().showDialog("Error", e.getMessage());
        e.printStackTrace();
        continue;
      }

      if (currentTagId == -1) {
        getNavigator().showDialog("Error", "Failed to add tag: " + tag);
        continue;
      }

      String q3 = "INSERT INTO cookbook.recipe_tag (recipeId, tagId) VALUES (?, ?)";

      try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(q3)) {
        stmt.setInt(1, recipeId);
        stmt.setInt(2, currentTagId);
        stmt.executeUpdate();
        System.out.println("Added tag: " + tag + " to recipe: " + recipeId);
      } catch (SQLException e) {
        getNavigator().showDialog("Error", "Failed to add tag to recipe: " + tag);
        e.printStackTrace();
      }
    }

    /**
     * Add the ingredients to the database
     */
    try {
      String[] ingredients = displayIngredients.getText().split("\\r?\\n");
      for (String ingredient : ingredients) {
        String[] parts = ingredient.split("\\s*,\\s*");

        String q4 = "INSERT INTO cookbook.unit (unitName) VALUES (?) ON DUPLICATE KEY UPDATE unitName=unitName";
        int currentUnitId = -1;
        int currentIngredientId = -1;

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(q4,
            Statement.RETURN_GENERATED_KEYS)) {
          stmt.setString(1, parts[2]);
          stmt.executeUpdate();
          System.out.println("Entered unit");

          Ingredient.addIngredientToDatabase(parts[0]);
          currentIngredientId = Ingredient.fromDB(parts[0]).getId();
          currentUnitId = cookbook.db.RecipeIngredient.Unit.fromDB(parts[2]).getUnitId();
          Ingredient.addIngredient(currentIngredientId, Double.parseDouble(parts[1]), currentUnitId, recipeId);

          if (currentUnitId == -1 || currentIngredientId == -1) {
            throw new Exception("Unable to add Ingredient: " + parts[0] + " to the database.");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      var item = getNavigator().getHistoryItem(Route.HOME);
      // update the Recipe's
      var controller = (HomeController) item.getController();

      controller.refreshRecipes();

      // go back to the home screen
      getNavigator().goBack();
    }
  }

  private int getTagIdFromName(String name) throws SQLException {
    String tagIdQuery = "SELECT tagId FROM cookbook.tag WHERE tagName = ?";
    try (PreparedStatement tagIdStatement = DB.getInstance().getConnection().prepareStatement(tagIdQuery)) {
      tagIdStatement.setString(1, name);
      try (ResultSet tagIdResultSet = tagIdStatement.executeQuery()) {
        if (tagIdResultSet.next()) {
          return tagIdResultSet.getInt("tagId");
        } else {
          throw new SQLException("Failed to retrieve the tagId.");
        }
      }
    }
  }

  @FXML
  private void recipeImageDragOver(DragEvent event) {

  }

  @FXML
  private void handleOnKeyReleasedIngredient(KeyEvent event) {
    if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
        || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
        || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END 
        || event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.SPACE) {
      return;
    }

    String enteredText = ingredientName.getEditor().getText();

    if (enteredText == null || enteredText.isEmpty()) {
      ingredientName.hide();
      return;
    }

    ArrayList<Ingredient> allAlike = Ingredient.getAllLike(enteredText);
    if (allAlike==null || allAlike.isEmpty()){
      ingredients.addAll(allAlike);
      ingredientName.hide();
    } else {
      ingredients.setAll(allAlike);
      ingredientName.show();
    }
  }

  @FXML
  private void recipeImageDragDropped() {
    // Create a FileChooser
    FileChooser fileChooser = new FileChooser();

    // Set title for the FileChooser dialog
    fileChooser.setTitle("Select an image");

    // Only allow images to be selected
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg",
        "*.gif");
    fileChooser.getExtensionFilters().add(extFilter);

    // Show open dialog
    File selectedFile = fileChooser.showOpenDialog(getNavigator().getPrimaryStage());

    // Process the selected file
    if (selectedFile != null) {
      this.selectedImage = selectedFile;

      // Create an Image object from the selected file
      Image image = new Image(this.selectedImage.toURI().toString());

      // Set the Image object to the ImageView
      recipeImage.setImage(image);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      instructions.setWrapText(true);
      displayIngredients.setEditable(false);
      ingredientName.setEditable(true);
      ingredientName.getEditor().setOnKeyReleased(this::handleOnKeyReleasedIngredient);
      ingredients = FXCollections.observableArrayList(Ingredient.getAll());
      ingredientName.setItems(ingredients);
      ingredientName.setConverter(new StringConverter<Ingredient>() {

        @Override
        public String toString(Ingredient ingredient) {
          return ingredient != null ? ingredient.getName() : "";
        }

        @Override
        public Ingredient fromString(String string) {
          if (string == null || string.isEmpty()) {
            ingredients = FXCollections.observableArrayList(Ingredient.getAll());
            return null;
          }
          
          // return the first ingredient that matches the entered text
          Ingredient ingredient = Ingredient.getAllLike(string).stream().findFirst().orElse(null);
          if (ingredient == null) {
            return null;
          } else {
            return ingredient;
          }
        }
      });

      ArrayList<cookbook.db.RecipeIngredient.Unit> units = cookbook.db.RecipeIngredient.Unit.getAll();
      for (var u : units) {
        unit.getItems().add(u.getUnitName());
      }

      DoubleSpinnerValueFactory amountFactory = new DoubleSpinnerValueFactory(0, 999);
      amount.setValueFactory(amountFactory);

      predefinedTags.getItems().addAll(arrayOfTags);
    } catch (Exception e) {
      getNavigator().showDialog("Error", "Error loading predefined tags, please try again. later");
    }
  }
}
