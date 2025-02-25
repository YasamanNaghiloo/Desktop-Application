package cookbook.controllers;

import cookbook.Ingredient;
import cookbook.db.IngredientDao;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controls the density scene.
 */
public class AddDensityController extends RouteController {
  private HashMap<String, Integer> ingredients;

  private IngredientDao ingredientDao;

  @FXML
  private Button addButton;

  @FXML
  private ComboBox<String> ingredientComboBox;

  @FXML
  private Spinner<Double> quantitySpinner;

  public AddDensityController() {
    ingredientDao = new IngredientDao();
  }

  /**
   * Initialization of the scene in this method.
   */
  @FXML
  public void initialize() {
    ingredients = new HashMap<>();

    ingredientComboBox.setEditable(true);

    // Add ingredients and its id to the hashmap.
    ArrayList<Ingredient> ingredients = ingredientDao.getIngredients();
    for (Ingredient ingred : ingredients) {

      this.ingredients.put(ingred.getName(), ingred.getId());

    }
    // Populate ingredientComboBox
    ingredientComboBox.setItems(FXCollections.observableArrayList(this.ingredients.keySet()));

    // Initialize the quantity spinner
    SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000000, 0,
        0.5);
    quantitySpinner.setValueFactory(valueFactory);
    // Make the spinner editable
    quantitySpinner.setEditable(true);

    // Set the density from the datatabase for the ingredient if there is one when
    // an ingredient is selected
    ingredientComboBox.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          ingredientComboBox.getEditor().positionCaret(ingredientComboBox.getEditor()
              .getText().length());
          if (newValue != null) {
            Integer id;
            try {
              id = this.ingredients.get(newValue);
              if (id != null) {
                Double density = ingredientDao.getDensity(id);
                if (density != null) {
                  quantitySpinner.getValueFactory().setValue(density);
                }
              }
              
            } catch (Exception e) {
              e.printStackTrace();
            }
          }

        });
  }

  @FXML
  private void handleOnKeyReleasedIngredient(KeyEvent event) {
    if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
        || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
        || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.SPACE
        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
      return;
    }

    String enteredText = ingredientComboBox.getEditor().getText();

    if (enteredText == null || enteredText.isEmpty()) {
      ingredientComboBox.hide();
      return;
    }
    String s = ingredientComboBox.getEditor().getText();
    final int cursorPosition = ingredientComboBox.getEditor().getCaretPosition();
    ingredientComboBox.setItems(FXCollections.observableArrayList(ingredients.keySet()));
    ingredientComboBox.setItems(filterItems(ingredientComboBox.getItems(), enteredText));
    ingredientComboBox.getEditor().setText(s);
    ingredientComboBox.getEditor().positionCaret(cursorPosition);
    if (ingredients.isEmpty()) {
      ingredientComboBox.hide();
    } else {
      ingredientComboBox.show();
    }

  }

  // Method to filter items based on the search string
  private ObservableList<String> filterItems(ObservableList<String> items, String searchString) {
    ObservableList<String> filteredItems = FXCollections.observableArrayList();
    for (String item : items) {
      if (item.toLowerCase().contains(searchString.toLowerCase())) {
        filteredItems.add(item);
      }
    }
    return filteredItems;
  }

  @FXML
  private void addDensity() {
    if (ingredients.containsKey(ingredientComboBox.getValue())) {
      // Get id from ingredient
      Integer id = ingredients.get(ingredientComboBox.getValue());
      // Get density
      Double density = quantitySpinner.getValue();
      if (density != null) {
        // Update the density
        ingredientDao.updateDensity(id, density);
        getNavigator().showDialog("Success", "Density set");
      } else {
        getNavigator().showDialog("Error", "Invalid density selected");
      }

    } else {
      getNavigator().showDialog("Error", "Invalid ingredient selected");
    }

  }
}
