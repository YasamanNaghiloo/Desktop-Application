package cookbook.controllers;

import cookbook.Ingredient;
import cookbook.TripleTuple;
import cookbook.Unit;
import cookbook.db.FridgeDao;
import cookbook.db.IngredientDao;
import cookbook.db.UnitDao;
import cookbook.navigator.Route;
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
 * Controls the add to fridge scene.
 */
public class AddToFridgeController extends RouteController {

  private HashMap<String, Integer> ingredients;
  private HashMap<String, Integer> units;

  @FXML
  private Button addButton;

  @FXML
  private ComboBox<String> ingredientComboBox;

  @FXML
  private Spinner<Double> quantitySpinner;

  @FXML
  private ComboBox<String> unitComboBox;

  /**
   * Initialization in this method.
   */
  @FXML
  public void initialize() {
    ingredients = new HashMap<>();
    units = new HashMap<>();
    unitComboBox.setEditable(true);
    ingredientComboBox.setEditable(true);

    FridgeDao fridgeDao = new FridgeDao();
    // Populate ingredientComboBox
    IngredientDao ingredientDao = new IngredientDao();
    ArrayList<Ingredient> ingredients = ingredientDao.getIngredients();
    ArrayList<Integer> ingredientIds = fridgeDao.getIngredientsInList();
    for (Ingredient ingredient : ingredients) {

      this.ingredients.put(ingredient.getName(), ingredient.getId());

    }
    ingredientComboBox.setItems(FXCollections.observableArrayList(this.ingredients.keySet()));
    UnitDao unitDao = new UnitDao();
    // Populate unitComboBox
    ArrayList<Unit> units = unitDao.getUnits();
    for (Unit unit : units) {
      this.units.put(unit.getName(), unit.getId());
    }
    unitComboBox.setItems(FXCollections.observableArrayList(this.units.keySet()));

    // Initialize the quantity spinner
    SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0001, 1000000, 1,
        0.5);
    quantitySpinner.setValueFactory(valueFactory);
    // Make the spinner editable
    quantitySpinner.setEditable(true);

    ingredientComboBox.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          ingredientComboBox.getEditor().positionCaret(ingredientComboBox
              .getEditor().getText().length());
        });

    unitComboBox.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          unitComboBox.getEditor().positionCaret(unitComboBox
              .getEditor().getText().length());
        });
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

  @FXML
  private void handleOnKeyReleasedUnit(KeyEvent event) {
    if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
        || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
        || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.SPACE
        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
      return;
    }

    String enteredText = unitComboBox.getEditor().getText();

    if (enteredText == null || enteredText.isEmpty()) {
      unitComboBox.hide();
      return;
    }
    String s = unitComboBox.getEditor().getText();
    final int cursorPosition = unitComboBox.getEditor().getCaretPosition();
    unitComboBox.setItems(FXCollections.observableArrayList(units.keySet()));
    unitComboBox.setItems(filterItems(ingredientComboBox.getItems(), enteredText));
    unitComboBox.getEditor().setText(s);
    unitComboBox.getEditor().positionCaret(cursorPosition);
    if (units.isEmpty()) {
      unitComboBox.hide();
    } else {
      unitComboBox.show();
    }

  }

  @FXML
  private void addToFridge() {
    IngredientDao ingredientDao = new IngredientDao();
    FridgeDao fridgeDao = new FridgeDao();
    if (units.containsKey(unitComboBox.getValue())) {
      if (ingredients.containsKey(ingredientComboBox.getValue())) {
        try {
          TripleTuple<Ingredient, Double, Unit> t = new TripleTuple<Ingredient, Double, Unit>(
              ingredientDao.getIngredient(ingredientDao
                  .getIngredientId(ingredientComboBox.getValue())),
              quantitySpinner.getValue(),
              new Unit(units.get(unitComboBox.getValue()), unitComboBox.getValue()));
          fridgeDao.inputFridgeIngredient(t, false);
          getNavigator().showDialog("Success", "Ingredient successfully added");
          FridgeController fridgeController = (FridgeController) getNavigator()
              .getHistoryItem(Route.FRIDGE)
              .getController();
          fridgeController.initialize();
        } catch (IllegalArgumentException e) {
          System.err.println(e);
        }
      } else {
        if (ingredientComboBox.getValue() != null && ingredientComboBox.getValue() != "") {
          try {
            TripleTuple<Ingredient, Double, Unit> t = new TripleTuple<Ingredient, Double, Unit>(
                new Ingredient(-1, ingredientComboBox.getValue().toString(), -1d),
                quantitySpinner.getValue(),
                new Unit(units.get(unitComboBox.getValue()), unitComboBox.getValue()));

            fridgeDao.inputFridgeIngredient(t, true);
            getNavigator().showDialog("Success", "New ingredient successfully added");
            FridgeController fridgeController = (FridgeController) getNavigator()
                .getHistoryItem(Route.FRIDGE)
                .getController();
            fridgeController.initialize();
          } catch (IllegalArgumentException e) {
            System.err.println(e);
          }
        } else {
          getNavigator().showDialog("Unsuccessful", "Not a valid ingredient");
        }

      }
    } else {
      getNavigator().showDialog("Unsuccessful", "Not a valid unit");
    }

  }
}
