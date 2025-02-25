package cookbook.controllers;

import cookbook.RecipeInterface;
import cookbook.db.Recipe;
import cookbook.db.ShoppingListDao;
import cookbook.dishlist.MealTime;
import cookbook.dishlist.MenuManager;
import cookbook.dishlist.WeekMenu;
import cookbook.dishlist.Weekday;
import cookbook.navigator.Route;
import cookbook.shoppinglist.IngredientsShoppingList;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * Controls the Weekly meal list class.
 */
public class FirstController extends RouteController {

  private MenuManager menuManager = new MenuManager();
  ArrayList<ListView<RecipeInterface>> listViews = new ArrayList<>();

  private Calendar calendar = Calendar.getInstance();

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private ListView<RecipeInterface> saturdayDinnerList;

  @FXML
  private ListView<RecipeInterface> fridayBreakfastList;

  @FXML
  private ListView<RecipeInterface> fridayDinnerList;

  @FXML
  private ListView<RecipeInterface> fridayLunchList;

  @FXML
  private GridPane gridveiw;

  @FXML
  private ListView<RecipeInterface> mondayBreakfastList;

  @FXML
  private ListView<RecipeInterface> mondayDinnerList;

  @FXML
  private ListView<RecipeInterface> mondayLunchList;

  @FXML
  private ListView<RecipeInterface> saturdayBreakfastList4;

  @FXML
  private ListView<RecipeInterface> saturdayLunchList;

  @FXML
  private ListView<RecipeInterface> sundayBreakfastList;

  @FXML
  private ListView<RecipeInterface> sundayDinnerList;

  @FXML
  private ListView<RecipeInterface> sundayLunchList;

  @FXML
  private ListView<RecipeInterface> thursdayBreakfastList;

  @FXML
  private ListView<RecipeInterface> thursdayDinnerList;

  @FXML
  private ListView<RecipeInterface> thursdayLunchList;

  @FXML
  private ListView<RecipeInterface> tuesdayBreakfastList;

  @FXML
  private ListView<RecipeInterface> tuesdayDinnerList;

  @FXML
  private ListView<RecipeInterface> tuesdayLunchList;

  @FXML
  private ListView<RecipeInterface> wednesdayBreakfastList;

  @FXML
  private ListView<RecipeInterface> wednesdayDinnerList;

  @FXML
  private ListView<RecipeInterface> wednesdayLunchList;

  @FXML
  private ListView<WeekMenu> weekListview;

  @FXML
  private ScrollPane weekscroll;

  @FXML
  private Spinner<Integer> weekSpinner;

  @FXML
  private Spinner<Integer> yearSpinner;

  @FXML
  private Button pdfCreate;

  @FXML
  private Button addButton;

  // Add ingredient to shoppinglist
  @FXML
  private void handleAdd() {
    ShoppingListDao shoppingListDao = new ShoppingListDao();
    WeekMenu w = menuManager.getCopyOfWeekMenu(getWeek(), getYear());
    ArrayList<RecipeInterface> recipes = w.getRecipes();
    for (RecipeInterface recipeInterface : recipes) {
      shoppingListDao.getIngredientsFromRecipetoDb(recipeInterface.getId());
    }
    getNavigator().showDialog("Successful", "Added ingredients from this week to shoppingList");

  }

  // Creates a shoppinglist
  @FXML
  private void createShoppingList() {
    IngredientsShoppingList ingredientsShoppingList = new IngredientsShoppingList();
    // Check if menu exists for that week.
    if (menuManager.weekMenuExists(weekSpinner.getValue(), yearSpinner.getValue())) {
      ingredientsShoppingList.createShoppinglistFromWeeklyDinnerList(menuManager
          .getCopyOfWeekMenu(weekSpinner.getValue(), yearSpinner.getValue())
          .getRecipes(), weekSpinner.getValue(), yearSpinner.getValue());
      getNavigator().showDialog("Success", "The pdf was successfully created at "
          + System.getProperty("user.dir") + "/ShoppingList/");
    } else {
      getNavigator().showDialog("Error", "Could not create pdf");
    }

  }

  /**
   * Called on initialization.
   */

  public void initialize() {
    // Initialize Spinners with their value factories
    IntegerSpinnerValueFactory yearFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2024, 9999);
    IntegerSpinnerValueFactory weekFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 53);

    // Set the initial values for the Spinners
    yearFactory.setValue(calendar.get(Calendar.YEAR));
    weekFactory.setValue(calendar.get(Calendar.WEEK_OF_YEAR));

    // Set the value factories for the Spinners
    yearSpinner.setValueFactory(yearFactory);
    weekSpinner.setValueFactory(weekFactory);

    addMenus();

    listViews.add(mondayBreakfastList);
    listViews.add(mondayLunchList);
    listViews.add(mondayDinnerList);
    listViews.add(tuesdayBreakfastList);
    listViews.add(tuesdayLunchList);
    listViews.add(tuesdayDinnerList);
    listViews.add(wednesdayBreakfastList);
    listViews.add(wednesdayLunchList);
    listViews.add(wednesdayDinnerList);
    listViews.add(thursdayBreakfastList);
    listViews.add(thursdayLunchList);
    listViews.add(thursdayDinnerList);
    listViews.add(fridayBreakfastList);
    listViews.add(fridayLunchList);
    listViews.add(fridayDinnerList);
    listViews.add(saturdayBreakfastList4);
    listViews.add(saturdayLunchList);
    listViews.add(saturdayDinnerList);
    listViews.add(sundayBreakfastList);
    listViews.add(sundayLunchList);
    listViews.add(sundayDinnerList);

    for (ListView<RecipeInterface> listView : listViews) {
      ObservableList<RecipeInterface> items = FXCollections.observableArrayList();
      listView.setItems(items);

      // Handle double-click events
      listView.setOnMouseClicked(event -> {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          RecipeInterface selectedItem = listView.getSelectionModel().getSelectedItem();
          if (selectedItem != null) {
            System.out.println("Double-clicked item: " + selectedItem);
            try {
              RecipeViewController controller = (RecipeViewController) getNavigator()
                  .push(Route.RECIPE_VIEW);
              controller.setData(Recipe.fromDB(selectedItem.getId()));
            } catch (Exception e) {
              System.err.println(e);
            }
          }
        }
      });
    }

    ObservableList<WeekMenu> items = FXCollections.observableArrayList();
    ArrayList<WeekMenu> menus = menuManager.getAllWeekMenusForYear(yearSpinner.getValue());
    for (WeekMenu weekMenu : menus) {
      items.add(weekMenu);
    }
    weekListview.setItems(items);

    // On selecting entry in list
    weekListview.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (newValue != null) {
            populateWeeklyDishGrid(newValue);
            // Get the value factory of the spinner
            SpinnerValueFactory<Integer> valueFactory = weekSpinner.getValueFactory();
  
            // Set the new value for the spinner
            valueFactory.setValue(newValue.getWeekNumber());
  
            // Get the value factory of the spinner
            SpinnerValueFactory<Integer> valueFactory2 = yearSpinner.getValueFactory();
  
            // Set the new value for the spinner
            valueFactory2.setValue(newValue.getYear());
          }
          
        });

    yearSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (menuManager.weekMenuExists(weekSpinner.getValue(), yearSpinner.getValue())) {
        populateWeeklyDishGrid(menuManager.getCopyOfWeekMenu(weekSpinner.getValue(),
            yearSpinner.getValue()));
      }
      setButtonVisibility(menuManager.weekMenuExists(weekSpinner.getValue(),
          yearSpinner.getValue()), pdfCreate);

      setButtonVisibility(menuManager.weekMenuExists(weekSpinner.getValue(),
          yearSpinner.getValue()), addButton);
      ObservableList<WeekMenu> items1 = FXCollections.observableArrayList();
      ArrayList<WeekMenu> menus1 = menuManager.getAllWeekMenusForYear(yearSpinner.getValue());
      weekListview.getItems().clear();
      for (WeekMenu weekMenu : menus1) {
        items1.add(weekMenu);
      }
      weekListview.setItems(items1);
    });

    weekSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (menuManager.weekMenuExists(weekSpinner.getValue(), yearSpinner.getValue())) {
        populateWeeklyDishGrid(menuManager.getCopyOfWeekMenu(weekSpinner.getValue(),
            yearSpinner.getValue()));
      }
      setButtonVisibility(menuManager.weekMenuExists(weekSpinner.getValue(),
          yearSpinner.getValue()), pdfCreate);
      setButtonVisibility(menuManager.weekMenuExists(weekSpinner.getValue(),
          yearSpinner.getValue()), addButton);
    });
  }

  // Populates the weekly dish grid
  private void populateWeeklyDishGrid(WeekMenu weekMenu) {
    if (weekMenu != null) {
      ArrayList<ArrayList<ArrayList<RecipeInterface>>> lines = weekMenu.getRecipeRepresentation();
      int listviewCount = 0;
      for (int j = 0; j < Weekday.values().length; j++) {
  
        for (int i = 0; i < MealTime.values().length; i++) {
          if (lines.get(j).size() == 0) {
            for (int k = 0; k < MealTime.values().length; k++) {
              lines.get(j).add(new ArrayList<>());
            }
          }
          populateListView(listViews.get(listviewCount), lines.get(j).get(i));
          listviewCount += 1;
  
        }
  
      }
    }
  }

  private void populateListView(ListView<RecipeInterface> listView,
      ArrayList<RecipeInterface> rows) {
    ObservableList<RecipeInterface> items = FXCollections.observableArrayList();
    items.addAll(rows);
    listView.setItems(items);

  }

  private void setButtonVisibility(boolean visible, Button button) {
    if (visible) {
      button.setVisible(true);
    } else {
      button.setVisible(false);
    }
  }

  private int getWeek() {
    return weekSpinner.getValue();
  }

  private int getYear() {
    return yearSpinner.getValue();
  }

  private void addMenus() {

    menuManager = new MenuManager();
    menuManager.loadMenus();
    setButtonVisibility(menuManager.weekMenuExists(weekSpinner.getValue(),
        yearSpinner.getValue()), pdfCreate);
    setButtonVisibility(menuManager.weekMenuExists(weekSpinner.getValue(),
        yearSpinner.getValue()), addButton);
  }

}