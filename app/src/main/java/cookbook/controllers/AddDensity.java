package cookbook.controllers;

import java.util.ArrayList;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.util.IntegerArray;
import com.google.protobuf.Int32Value;
import java.util.HashMap;
import cookbook.Ingredient;
import cookbook.TripleTuple;
import cookbook.Unit;
import cookbook.db.DB;
import cookbook.db.DatabaseStatement;
import cookbook.db.IngredientDao;
import cookbook.db.ShoppingListDao;
import cookbook.db.UnitDao;
import cookbook.dishlist.DayMenu;
import cookbook.dishlist.Weekday;
import cookbook.fridge.Fridge;
import cookbook.navigator.Route;
import cookbook.shoppinglist.IngredientsShoppingList;
import impl.org.controlsfx.collections.MappingChange.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class AddDensity extends RouteController {

  private HashMap<String, Integer> ingredients;
  private HashMap<String, Integer> units;
  private ShoppingListDao shoppingListDao;

  @FXML
  private Button addButton;

  @FXML
  private ComboBox<String> ingredientComboBox;

  @FXML
  private Spinner<Double> quantitySpinner;
  

  @FXML
  public void initialize() {
    shoppingListDao = new ShoppingListDao();
    ingredients = new HashMap<>();
    units = new HashMap<>();

    // Make the ComboBoxes searchable
    makeComboBoxSearchableIngredient(ingredientComboBox);

    IngredientsShoppingList ingredientsShoppingList = new IngredientsShoppingList();
    // Populate ingredientComboBox
    IngredientDao ingredientDao = new IngredientDao();
    ArrayList<Ingredient> ingredients = ingredientDao.getIngredients();

    ingredientComboBox.setItems(FXCollections.observableArrayList(this.ingredients.keySet()));

    // Initialize the quantity spinner
    SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.001, 10000, 0.5,
        0.5);
    quantitySpinner.setValueFactory(valueFactory);
    // Make the spinner editable
    quantitySpinner.setEditable(true);
    for (Ingredient ingred : ingredients) {
      this.ingredients.put(ingred.getName(), ingred.getId());

    }
  }

  // Method to make a ComboBox searchable
  private void makeComboBoxSearchableIngredient(ComboBox<String> comboBox) {
    comboBox.setEditable(true);
    comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      IngredientDao ingredientDao = new IngredientDao();
      if (ingredients.get(newValue) != null) {
        Double t = (ingredientDao.getDensity(ingredients.get(newValue)));
        if (t != null) {
          quantitySpinner.getValueFactory().setValue(t);
        }
      }
      comboBox.setItems(FXCollections.observableArrayList(ingredients.keySet()));
      comboBox.setItems(filterItems(comboBox.getItems(), newValue));
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
  private void addToFridge() {
  }


  private Double getquantiy() {
    Double quantity = quantitySpinner.getValue();
    if (quantity != null) {
      if (quantity > 0) {
        return quantity;
      } else {
        throw new IllegalArgumentException("The quantity has to be larger than 0");
      }
    } else {
      throw new IllegalArgumentException("The quantity has not been selected");
    }
  }

  /*   private String getunit() {
    UnitDao unitDao = new UnitDao();
    String unit = unitComboBox.getValue();
    if (unit != null && unitDao.getUnits().contains(unit)) {
      return unit;
    } else {
      throw new IllegalArgumentException("A valid unit has not been selected");
    }
  }
  */

  // TODO: ADD SUPPORT FOR DOUBLE QUANTITIES IN DATABASE AND HERE
  private String getIngredient() {
    String ingredient = ingredientComboBox.getValue();
    if (ingredient != null && ingredient != " ") {
      return ingredient;
    } else {
      throw new IllegalArgumentException("The ingredient field can not be empty");
    }
  }
}