package cookbook.controllers;

import cookbook.Ingredient;
import cookbook.TripleTuple;
import cookbook.Unit;
import cookbook.db.FridgeDao;
import cookbook.fridge.CustomCellFactory;
import cookbook.navigator.Route;

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
 * Controls the fridge view.
 */
public class FridgeController extends RouteController {

  private boolean editable;
  private FridgeDao fridgeDao;

  private CustomCellFactory cellFactory;

  @FXML
  private Button editButton;

  @FXML
  private Button addButton;

  @FXML
  private ListView<TripleTuple<cookbook.Ingredient, Double, Unit>> fridgeView;

  @FXML
  void addFridge(ActionEvent event) {
    getNavigator().pushAsPopup(Route.ADD_TO_FRIDGE, 300);
  }

  // Disable edit mode when exiting page
  @Override
  public void onRouteLoaded() {
    getNavigator().registerPopCallback(() -> {
      if (editable) {
        ObservableList<TripleTuple<cookbook.Ingredient, Double, Unit>> items = fridgeView.getItems();
        for (TripleTuple<cookbook.Ingredient, Double, Unit> tripleTuple : items) {
          fridgeDao.updateIngredientInFridge(tripleTuple, tripleTuple.third);
        }
      }
    });
  }

  @FXML
  void editFridge(ActionEvent event) throws IOException {
    if (!editable) {
      editButton.setText("Save");

    } else {
      editButton.setText("Edit");
      ObservableList<TripleTuple<cookbook.Ingredient, Double, Unit>> items = fridgeView.getItems();
      for (TripleTuple<cookbook.Ingredient, Double, Unit> tripleTuple : items) {
        fridgeDao.updateIngredientInFridge(tripleTuple, tripleTuple.third);
      }
    }
    editable = !editable;
    addButton.setDisable(editable);
    setEditmode(editable);
  }

  @FXML
  void empty(ActionEvent event) {
    fridgeView.getItems().clear();
    fridgeDao.emptyFridge();
  }

  /**
   * Prepares the content of the view.
   */
  public void initialize() {
    fridgeDao = new FridgeDao();
    cellFactory = new CustomCellFactory();
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
    ArrayList<TripleTuple<Ingredient, Double, Unit>> items = fridgeDao.getFridgeIngredients();
    for (TripleTuple<Ingredient, Double, Unit> tripleTuple : items) {
      fridgeItems.add(tripleTuple);
    }

    return fridgeItems;
  }

  /*
   * Sets the buttons, spinners and comboboxes to be editable or not editable.
   */
  private void setEditmode(boolean editable) {
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
