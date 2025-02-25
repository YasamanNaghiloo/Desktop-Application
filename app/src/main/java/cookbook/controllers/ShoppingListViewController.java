package cookbook.controllers;

import cookbook.Ingredient;
import cookbook.TripleTuple;
import cookbook.Unit;
import cookbook.db.FridgeDao;
import cookbook.db.IngredientDao;
import cookbook.db.ShoppingListDao;
import cookbook.navigator.Route;
import cookbook.searcher.SearchInFile;
import cookbook.shoppinglist.CustomCellShopping;
import cookbook.shoppinglist.IngredientsShoppingList;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;

/**
 * Controls shopping List view.
 */
public class ShoppingListViewController extends RouteController {

  private CustomCellShopping cellFactory;
  private ShoppingListDao shoppingListDao;
  private boolean editable;
  @FXML
  private ListView<TripleTuple<Ingredient, Double, Unit>> fridgeView;

  @FXML
  private Button editButton;

  @FXML
  private Button pdfButton;

  @FXML
  private Button emptyButton;

  @FXML
  private Button addButton;

  /**
   * Syncs the ingredients in the fridges with the ingredients in the
   * shoppinglist.
   *
   * @param event event
   */
  @FXML
  void sync(ActionEvent event) {
    FridgeDao fridgeDao = new FridgeDao();
    SearchInFile searchInFile = new SearchInFile();
    IngredientDao ingredientDao = new IngredientDao();
    ArrayList<TripleTuple<Ingredient, Double, Unit>> fridgeItem = fridgeDao.getFridgeIngredients();
    ObservableList<TripleTuple<Ingredient, Double, Unit>> listItem = fridgeView.getItems();
    // Array of items which could not be synced because of either no density
    // or unconvertable unit.
    ArrayList<TripleTuple<Ingredient, Double, Unit>> unableToSyncItems = new ArrayList<>();
    ArrayList<TripleTuple<Ingredient, Double, Unit>> syncedItems = new ArrayList<>();

    for (TripleTuple<Ingredient, Double, Unit> item : listItem) {
      for (TripleTuple<Ingredient, Double, Unit> frItem : fridgeItem) {
        if (frItem.first.getId() == item.first.getId()) {

          Double quantity = null;
          if (frItem != null) {
            if (!frItem.third.equals(item.third)) {
              // Try to convert the quantity to correct unit.
              try {
                quantity = searchInFile.convertToUnit(
                    item.first.getName(), frItem.second, frItem.third, item.third, ingredientDao
                        .getDensity(item.first.getId()));

              } catch (IllegalArgumentException e) {

              }

            } else {
              quantity = frItem.second;
            }
          }

          if (quantity != null) {
            syncedItems.add(frItem);
            // Calculate the difference and remove the ingredient if enough if in the fridge
            // already.
            double difference;
            if (quantity != 0) {
              difference = item.second - quantity;
            } else {
              difference = item.second - frItem.second;
            }

            if (difference > 0) {
              // Set the new quantity
              TripleTuple<Ingredient, Double, Unit> updateItem = new TripleTuple<Ingredient, Double, Unit>(item.first,
                  difference, item.third);
              shoppingListDao.updateIngredientInShoppingList(updateItem, item.third);
              break;
            } else {
              // Delete if enough if in fridge
              shoppingListDao.deleteFromDatabase(item.first.getId(), item.third.getId());
              break;
            }
          } else {
            if (!unableToSyncItems.contains(frItem)) {
              unableToSyncItems.add(frItem);
            }  
          }

        }

      }

    }
    for (TripleTuple<Ingredient, Double, Unit> syncItem : syncedItems) {

      if (unableToSyncItems.contains(syncItem)) {
        unableToSyncItems.remove(syncItem);
        System.err.println("r" + syncItem);
      }
    }
    // Create string of all items that was unable to be synced
    StringBuilder stringBuilder = new StringBuilder(100);
    for (TripleTuple<Ingredient, Double, Unit> tripleTuple : unableToSyncItems) {
      stringBuilder.append(tripleTuple.first.getName() + " "
          + Math.round(tripleTuple.second * 10) / 10 + " " + tripleTuple.third.getName() + "\n");
    }
    String result = stringBuilder.toString();
    if (unableToSyncItems.size() > 0) {
      getNavigator().showDialog("Information", "The following ingredients: "
          + result + "could not be synced from fridge since"
          + " the unit could not be converted");
    } else {
      getNavigator().showDialog("Notice", "Ingredients synced");

    }
    initialize();
  }

  // Disable edit mode when exiting page
  @Override
  public void onRouteLoaded() {
    getNavigator().registerPopCallback(() -> {
      if (editable) {
        ObservableList<TripleTuple<Ingredient, Double, Unit>> items = fridgeView.getItems();
        for (TripleTuple<Ingredient, Double, Unit> tripleTuple : items) {
          shoppingListDao.updateIngredientInShoppingList(tripleTuple, tripleTuple.third);
        }
      }
    });
  }

  /**
   * Prepares the list to be ready to be edited or to stop being edited.
   *
   * @param event event
   */
  @FXML
  void editShoppingList(ActionEvent event) {
    if (!editable) {
      editButton.setText("Save");

    } else {
      // Save the edited items
      editButton.setText("Edit");
      ObservableList<TripleTuple<Ingredient, Double, Unit>> items = fridgeView.getItems();
      for (TripleTuple<Ingredient, Double, Unit> tripleTuple : items) {
        shoppingListDao.updateIngredientInShoppingList(tripleTuple, tripleTuple.third);
      }
    }
    editable = !editable;
    addButton.setDisable(editable);
    try {
      setEditmode(editable);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void createPdf(ActionEvent event) {
    IngredientsShoppingList ingredientsShoppingList = new IngredientsShoppingList();
    ingredientsShoppingList.createShoppinglist();
    getNavigator().showDialog("Success", "The pdf was successfully created at : "
        + System.getProperty("user.dir") + "/ShoppingList/");
  }

  /**
   * Empty the fridge.
   *
   * @param event button click
   */
  @FXML
  void empty(ActionEvent event) {
    fridgeView.getItems().clear();
    shoppingListDao.emptyShoppingList();
    // Set the editmode to be correct.
    if (editable) {
      editable = false;
      try {
        editButton.setText("Save");
        setEditmode(editable);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Goes to add to shoppinglist scene.
   *
   * @param event button click
   */
  @FXML
  public void add(ActionEvent event) {
    getNavigator().pushAsPopup(Route.ADD_TO_SHOPPING_LIST, 300);
  }

  /**
   * Run on initialization of view.
   */
  public void initialize() {
    shoppingListDao = new ShoppingListDao();
    cellFactory = new CustomCellShopping();
    fridgeView.setCellFactory(cellFactory);
    editable = false;

    // Provide data to display in the ListView
    ObservableList<TripleTuple<Ingredient, Double, Unit>> fridgeItems = getDataForFridge();
    fridgeView.setItems(fridgeItems);

  }

  // Method to provide data for the ListView
  private ObservableList<TripleTuple<Ingredient, Double, Unit>> getDataForFridge() {
    ObservableList<TripleTuple<Ingredient, Double, Unit>> fridgeItems = FXCollections
        .observableArrayList();
    ArrayList<TripleTuple<Ingredient, Double, Unit>> items = shoppingListDao.getShoppingListItems();

    for (TripleTuple<Ingredient, Double, Unit> tripleTuple : items) {
      fridgeItems.add(tripleTuple);
    }

    return fridgeItems;
  }

  // Set the editmode of the elements in the listview
  private void setEditmode(boolean editable) throws IOException {
    ArrayList<ComboBox<Unit>> comboBoxs = cellFactory.getComboBoxList();
    ArrayList<Spinner<Double>> spinners = cellFactory.getSpinnerList();
    ArrayList<Button> listButton = cellFactory.getDeleteButtons();
    if (comboBoxs.size() > 0) {
      for (int i = 0; i < comboBoxs.size(); i++) {
        comboBoxs.get(i).setDisable(!editable);
        spinners.get(i).setDisable(!editable);
        spinners.get(i).setEditable(editable);
        listButton.get(i).setVisible(editable);
      }

    }

  }

}
