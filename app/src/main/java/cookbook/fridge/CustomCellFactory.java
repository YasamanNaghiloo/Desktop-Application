package cookbook.fridge;

import cookbook.Ingredient;
import cookbook.TripleTuple;
import cookbook.Unit;
import cookbook.db.FridgeDao;
import cookbook.db.IngredientDao;
import cookbook.db.UnitDao;
import cookbook.searcher.SearchInFile;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Controls the custom cells in the lsitview in fridge list.
 */
public class CustomCellFactory implements
    Callback<ListView<TripleTuple<Ingredient, Double, Unit>>, ListCell<TripleTuple<Ingredient, Double, Unit>>> {

  private ArrayList<Unit> volumeUnits = new ArrayList<>();
  private ArrayList<Unit> massUnits = new ArrayList<>();
  private ArrayList<TextField> textFieldList = new ArrayList<>();
  private ArrayList<Spinner<Double>> spinnerList = new ArrayList<>();
  private ArrayList<ComboBox<Unit>> comboBoxList = new ArrayList<>();
  private ArrayList<Button> buttonList = new ArrayList<>();
  private boolean ignoreComboBoxAction;

  @Override
  public ListCell<TripleTuple<Ingredient, Double, Unit>> call(
      ListView<TripleTuple<Ingredient, Double, Unit>> param) {
    return new ListCell<>() {
      private final HBox hbox = new HBox();
      private final TextField textField = new TextField();
      private final Spinner<Double> spinner = new Spinner<>(0.00001, 1000000000, 0.5, 0.5);
      private final ComboBox<Unit> comboBox = new ComboBox<>();
      private final Button removeButton = new Button("Remove");

      {
        UnitDao unitDao = new UnitDao();
        volumeUnits = unitDao.getUnitsofType("volume");
        massUnits = unitDao.getUnitsofType("mass");
        textField.setAlignment(Pos.CENTER_LEFT);
        comboBox.setStyle("-fx-alignment: center-left;");
        // Add components to the VBox
        hbox.getChildren().addAll(textField, spinner, comboBox, removeButton);

        spinner.setOpacity(1);
        comboBox.setOpacity(1);
        spinner.setEditable(false);
        spinner.setDisable(true);
        comboBox.setEditable(true);
        comboBox.setDisable(true);
        removeButton.setVisible(false);

        comboBox.setConverter(new StringConverter<Unit>() {
          @Override
          public String toString(Unit object) {
            if (object == null) {
              return null;
            }
            return object.toString();
          }

          @Override
          public Unit fromString(String s) {
            UnitDao unitDao = new UnitDao();
            Integer id = unitDao.getUnitId(s);
            if (id == null) {
              System.err.println("Error, could not find unitId in database");
              return new Unit(0, s);
            }
            return new Unit(id, s);
          }
        });

        // Define the action for the remove button
        removeButton.setOnAction(event -> {
          // Retrieve the item associated with this cell
          TripleTuple<Ingredient, Double, Unit> item = getItem();
          FridgeDao fridgeDao = new FridgeDao();
          fridgeDao.deleteFromDatabase(item.first.getId(), item.third.getId());
          // Remove the item from the ListView
          param.getItems().remove(item);
        });

        // Update the value of item when the spinner value changes
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
          // Get the index of the current item in the ListView
          int index = getIndex();
          // Get the current list of items in the ListView
          ObservableList<TripleTuple<Ingredient, Double, Unit>> items = getListView().getItems();
          items.get(index).setSecond(newValue);
        });

        // Check if you can combine entries in the shoppinglist
        comboBox.setOnAction(event -> {
          if (!ignoreComboBoxAction) {
            IngredientDao ingredientDao = new IngredientDao();
            Unit newValue = comboBox.getSelectionModel().getSelectedItem();
            int index = getIndex();
            // Get the current list of items in the ListView
            ObservableList<TripleTuple<Ingredient, Double, Unit>> items = getListView().getItems();
            // Get the current item from the list
            TripleTuple<Ingredient, Double, Unit> currentItem = items.get(index);
            SearchInFile searchInFile = new SearchInFile();
            TripleTuple<Ingredient, Double, Unit> updatedItem;
            FridgeDao fridgeDao = new FridgeDao();
            if (newValue != null && currentItem.third != null && !newValue.equals(currentItem.third)) {
              if (fridgeDao.getIngredientInFridge(items.get(index).first.getId()).size() > 1) {
                ArrayList<Integer> indexesWithIngredient = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                  if (items.get(i).first.getName().equals(currentItem.first.getName())) {
                    if (!currentItem.equals(items.get(i))) {
                      indexesWithIngredient.add(i);
                    }
                  }
                }
                // Try to combine multiple entries.
                for (int i = 0; i < indexesWithIngredient.size(); i++) {
                  try {
                    if (newValue.equals(items.get(indexesWithIngredient.get(i)).third)) {
                      fridgeDao.deleteFromDatabase(currentItem.first.getId(),
                          currentItem.third.getId());
                      TripleTuple<Ingredient, Double, Unit> t = items
                          .get(indexesWithIngredient.get(i));
                      t.setSecond(currentItem.second + t.second);
                      fridgeDao.inputFridgeIngredient(t, false);
                      items.get(indexesWithIngredient.get(i)).setSecond(t.second);
                      items.get(indexesWithIngredient.get(i)).setThird(newValue);
                      items.remove(index);
                      // Reload the list
                      ObservableList<TripleTuple<Ingredient, Double, Unit>> newList = FXCollections
                          .observableArrayList(getListView().getItems());
                      reloadList(getListView(), newList);
                        return;

                    } else {
                      try {
                        // Try to convert
                        Double convertedQuantity = searchInFile
                            .convertToUnit(currentItem.first.getName(),
                                currentItem.second, newValue, items
                                    .get(indexesWithIngredient.get(i)).third,
                                ingredientDao.getDensity(currentItem.first.getId()));
                        fridgeDao.deleteFromDatabase(currentItem.first.getId(),
                            currentItem.third.getId());
                        TripleTuple<Ingredient, Double, Unit> t = items
                            .get(indexesWithIngredient.get(i));
                        t.setSecond(convertedQuantity + t.second);
                        fridgeDao.inputFridgeIngredient(t, false);
                        items.get(indexesWithIngredient.get(i)).setSecond(t.second);
                        items.get(indexesWithIngredient.get(i)).setThird(items
                            .get(indexesWithIngredient.get(i)).third);
                        items.remove(index);
                        // Reload list
                        ObservableList<TripleTuple<Ingredient, Double, Unit>> newList = FXCollections
                          .observableArrayList(getListView().getItems());
                      reloadList(getListView(), newList);
                        return;
                      } catch (IllegalArgumentException e) {
                        System.err.println(e);
                      }
                    }

                  } catch (IllegalArgumentException e) {

                  }
                }

              }
              try {
                // Try to convert
                Double convertedQuantity = searchInFile.convertToUnit(currentItem.first.getName(),
                    currentItem.second, currentItem.third, newValue, ingredientDao
                        .getDensity(currentItem.first.getId()));
                if (convertedQuantity != null) {
                  // Set values
                  spinner.getValueFactory().setValue(convertedQuantity);
                  items.get(index).setSecond(convertedQuantity);
                  Unit oldUnit = currentItem.third;
                  items.get(index).setThird(newValue);
                  if (oldUnit != null && newValue != null) {
                    fridgeDao.updateIngredientInFridge(currentItem, oldUnit);
                  }
                }

              } catch (IllegalArgumentException e) {
                Unit oldUnit = currentItem.third;
                if (oldUnit != null && newValue != null) {
                  items.get(index).setThird(newValue);
                  fridgeDao.updateIngredientInFridge(currentItem, oldUnit);
                }
              }

            } else {
              // If the units are of the same unittype
              if (massUnits.contains(newValue) && massUnits.contains(currentItem.third)
                  || volumeUnits.contains(newValue) && volumeUnits.contains(currentItem.third)) {
                try {
                  Double convertedQuantity = searchInFile.convertToUnit(currentItem.first.getName(),
                      currentItem.second, currentItem.third, newValue, ingredientDao
                          .getDensity(currentItem.first.getId()));

                  if (convertedQuantity != null) {
                    spinner.getValueFactory().setValue(convertedQuantity);
                    items.get(index).setSecond(convertedQuantity);
                    Unit oldUnit = currentItem.third;

                    if (oldUnit != null && newValue != null) {
                      items.get(index).setThird(newValue);
                      fridgeDao.updateIngredientInFridge(currentItem, oldUnit);
                    }
                  }

                } catch (IllegalArgumentException e) {
                  Unit oldUnit = currentItem.third;
                  items.get(index).setThird(newValue);
                  if (oldUnit != null) {
                    fridgeDao.updateIngredientInFridge(currentItem, oldUnit);
                  }
                }
              }

            }

            // Replace the item at the current index with the updated item

            items.get(index).setThird(newValue);

          }
        });

      }

      @Override
      protected void updateItem(TripleTuple<Ingredient, Double, Unit> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setGraphic(null);
        } else {
          // Set the graphic of the cell to the VBox
          setGraphic(hbox);
          ignoreComboBoxAction = true;
          // Set data to components
          textField.setText(item.first.getName());
          spinner.getValueFactory().setValue(item.second);
          Unit u = item.third;
          UnitDao unitDao = new UnitDao();
          ArrayList<Unit> units = unitDao.getUnits();
          ArrayList<Unit> correctUnits = new ArrayList<>();

          try {
            correctUnits.addAll(units);
          } catch (Exception e) {
            e.printStackTrace();
          }
          comboBox.setItems(FXCollections.observableArrayList(correctUnits));
          comboBox.getSelectionModel().select(u);
          textField.setEditable(false);

          // Check if the spinner has been changed
          if (spinner.isDisabled()) {
            spinner.setEditable(false);
            spinner.setDisable(true);
          } else {
            spinner.setEditable(true);
            spinner.setDisable(false);
          }

          // Check if the spinner has been changed
          if (comboBox.isDisabled()) {
            comboBox.setDisable(true);
          } else {
            comboBox.setDisable(false);
          }
          // Add componets to list so that their edimode can be set from shoppingview controller class
          spinnerList.add(spinner);
          comboBoxList.add(comboBox);
          textFieldList.add(textField);
          buttonList.add(removeButton);
          ignoreComboBoxAction = false;
        }
      }
    };
  }

  //Reloads the list
  public void reloadList(ListView<TripleTuple<Ingredient, Double, Unit>> listView,
      List<TripleTuple<Ingredient, Double, Unit>> newItems) {
    ObservableList<TripleTuple<Ingredient, Double, Unit>> items = listView.getItems();
    items.clear();
    items.addAll(newItems);
  }

  public ArrayList<TextField> getTextFieldList() {
    return textFieldList;
  }

  public ArrayList<Spinner<Double>> getSpinnerList() {
    return spinnerList;
  }

  public ArrayList<ComboBox<Unit>> getComboBoxList() {
    return comboBoxList;
  }

  public ArrayList<Button> getDeleteButtons() {
    return buttonList;
  }

}
