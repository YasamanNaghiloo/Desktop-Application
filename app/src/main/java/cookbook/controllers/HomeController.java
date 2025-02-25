package cookbook.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import cookbook.RecipeInterface;
import cookbook.auth.Auth;
import cookbook.db.Ingredient;
import cookbook.db.Recipe;
import cookbook.db.Tag;
import cookbook.dishlist.MealTime;
import cookbook.dishlist.MenuManager;
import cookbook.dishlist.Weekday;
import cookbook.navigator.Route;
import cookbook.server.Server;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class HomeController extends RouteController implements Initializable {
  @FXML
  private ImageView profileImage;

  @FXML
  private Label displayNameLabel;

  @FXML
  private GridPane grid;

  @FXML
  private GridPane tagsGrid;

  @FXML
  private GridPane ingredientsGrid;

  @FXML
  private Label selTagsLabel;

  @FXML
  private Label selIngredientsLabel;

  @FXML
  private Button adminButton;

  @FXML
  private Button tutorial;

  @FXML
  private TextField searchField;

  @FXML
  private HBox filterButton;

  @FXML
  private Button displayMessages;

  @FXML
  private Button myRecipesButton;

  @FXML
  private Button allRecipesButton;

  @FXML
  private Button dishListButton;

  @FXML
  private Button favoritesButton;

  private ArrayList<Recipe> recipes = new ArrayList<>();

  private PopOver filterPopOver;

  private enum Tab {
    ALL_RECIPES,
    MY_RECIPES,
    FAVORITES,
    DISH_LIST
  }

  private Tab currentTab = Tab.MY_RECIPES;

  private final int TAG_GRID_COLUMN_COUNT = 3;
  private final int INGREDIENT_GRID_COLUMN_COUNT = 3;

  private ArrayList<Integer> addedTagIds = new ArrayList<>();
  private ArrayList<Integer> addedIngredientIds = new ArrayList<>();

  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      updateDisplayName();
      updateProfilePicture();
      
      recipes.addAll(Recipe.getAllFromUser(Auth.currentUser().getId()));
      searchField.textProperty().addListener(this::search);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (Auth.currentUser().isAdmin()) {
      adminButton.setVisible(true);
    }
  }

  @Override
  public void onRouteLoaded() {
    // We need to call this separately from initialize because the navigator is not
    // available at that point
    initGrids();
  }

  public void updateDisplayName() {
    displayNameLabel.setText(Auth.currentUser().getDisplayName());
  }

  public void updateProfilePicture() {
    if (Auth.currentUser().getImagePath() == null) {
      Image defaultProfilePicture = new Image(getClass().getResourceAsStream("/DefaultProfilePicture.png"));
      profileImage.setImage(defaultProfilePicture);
    } else {
      String url = Server.pathToUrl(Auth.currentUser().getImagePath());
      System.out.println(url);
      Image userImage = new Image(url);
      profileImage.setImage(userImage);
    }

    Circle clip = new Circle(profileImage.getFitWidth() / 2, profileImage.getFitHeight() / 2, profileImage.getFitWidth() / 2);
    profileImage.setClip(clip);
  }

  public boolean isFilterPopOverShowing() {
    return filterPopOver.isShowing();
  }

  private void initGrids() {
    /**
     * ======= Init the Recipe Grid =======
     */
    redrawGrid(calcColumns(getNavigator().getPrimaryStage().getScene().getWidth()));
    // Redraw the grid when the window is resized
    getNavigator().getPrimaryStage()
        .getScene()
        .widthProperty()
        .addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
          if (dishListButton.isDisable()) {
            redrawGridWithGridDataDishList(calcColumns(newSceneWidth.doubleValue()));
          } else {
            redrawGridWithGridData(calcColumns(newSceneWidth.doubleValue()));
          }
        });
    /**
     * ======= Init selected tags Grid =======
     */
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/home/FilterPopOver.fxml"));
      Parent root = loader.load();
      FilterController filterController = loader.getController();
      filterController.injectNavigator(getNavigator());

      filterPopOver = new PopOver(root);
      filterPopOver.setDetachable(false);
      filterPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
      filterPopOver.setAutoHide(false);
    } catch (IOException e) {
      System.out.println("Error loading filter pop over: " + e.getMessage());
    }
  }

  /**
   * Refresh the recipes in the grid
   */
  public void refreshRecipes() {
    // call search again to refresh the recipes
    search(null, null, searchField.getText());
    // redraw the grid with the new data
    redrawGrid(calcColumns(getNavigator().getPrimaryStage().getScene().getWidth()));
  }

  public int calcColumns(double width) {
    if (width < 600) {
      return 1;
    }
    return ((int) width - 400) / 200;
  }

  public void switchTab(Tab tab) {
    currentTab = tab;
    // Disable the search field and filter button if the dish list is shown
    searchField.setDisable(tab == Tab.DISH_LIST);
    filterButton.setDisable(tab == Tab.DISH_LIST);

    switch (tab) {
      case ALL_RECIPES -> {
        allRecipesButton.setDisable(true);
        myRecipesButton.setDisable(false);
        favoritesButton.setDisable(false);
        dishListButton.setDisable(false);
        search(null, null, searchField.getText());
      }
      case MY_RECIPES -> {
        allRecipesButton.setDisable(false);
        myRecipesButton.setDisable(true);
        favoritesButton.setDisable(false);
        dishListButton.setDisable(false);
        search(null, null, searchField.getText());
      }
      case FAVORITES -> {
        recipes = Recipe.getAllFavorites(Auth.currentUser().getId());
        allRecipesButton.setDisable(false);
        myRecipesButton.setDisable(false);
        favoritesButton.setDisable(true);
        dishListButton.setDisable(false);
        search(null, null, searchField.getText());
      }
      case DISH_LIST -> {
        loadDishList();
      }
    }
  }

  public void loadDishList() {
    MenuManager menuManager = new MenuManager();
    searchField.setText("");
    tagsGrid.getChildren().clear();
    ingredientsGrid.getChildren().clear();
    addedTagIds.clear();
    addedIngredientIds.clear();
    try {
      menuManager.loadMenus();
    } catch (Exception e) {
      // ERRORHANDLING
      System.err.println(e);
    }
    LocalDate now = LocalDate.now();
    WeekFields weekFields = WeekFields.of(Locale.getDefault());
    ArrayList<Recipe> recipes = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<RecipeInterface>>> rep = new ArrayList<>();
    if (menuManager.weekMenuExists(now.get(weekFields.weekOfWeekBasedYear()),
        now.getYear())) {
      ArrayList<RecipeInterface> recipeInterfaces = menuManager
          .getCopyOfWeekMenu(now.get(weekFields.weekOfWeekBasedYear()),
              now.getYear())
          .getRecipes();

      rep = menuManager.getCopyOfWeekMenu(now.get(weekFields.weekOfWeekBasedYear()),
          now.getYear()).getRecipeRepresentation();

      for (RecipeInterface recipeInterface : recipeInterfaces) {
        if (recipeInterface instanceof Recipe) {
          recipes.add((Recipe) recipeInterface);
        } else {
          System.err.println("Element not instance of Recipe class");
        }
      }

    } else {
      getNavigator().showDialog("Information", "There is no weekly dinner for this week");
    }
    this.recipes = recipes;
    allRecipesButton.setDisable(false);
    myRecipesButton.setDisable(false);
    favoritesButton.setDisable(false);
    dishListButton.setDisable(true);
    redrawGridDishList(calcColumns(getNavigator().getPrimaryStage().getScene().getWidth()), rep);
  }

  public void search(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    System.out.println("Searching for: " + newValue + " With the tags: " + addedTagIds + " And the ingredients: "
        + addedIngredientIds);

    newValue = newValue.trim();

    if (currentTab == Tab.DISH_LIST) {
      return;
    }

    // Skip searching for tags/ingredients if none are selected
    if (addedTagIds.isEmpty() && addedIngredientIds.isEmpty()) {
      recipes = switch (currentTab) {
        case ALL_RECIPES -> Recipe.getAllLike(newValue);
        case MY_RECIPES -> Recipe.getAllFromUserLike(Auth.currentUser().getId(), newValue);
        case FAVORITES -> Recipe.getAllFavoritesLike(Auth.currentUser().getId(), newValue);
        case DISH_LIST -> recipes;
      };
    } else {
      recipes = switch (currentTab) {
        case ALL_RECIPES -> Recipe.searchAll(newValue, addedTagIds, addedIngredientIds);
        case MY_RECIPES ->
          Recipe.searchAllFromUser(Auth.currentUser().getId(), newValue, addedTagIds, addedIngredientIds);
        case FAVORITES ->
          Recipe.searchAllFavorites(Auth.currentUser().getId(), newValue, addedTagIds, addedIngredientIds);
        case DISH_LIST -> recipes;
      };
    }
    redrawGrid(calcColumns(getNavigator().getPrimaryStage().getScene().getWidth()));
  }

  /**
   * Redraw the grid. Warning: Super slow because of so many FXML loaders; avoid
   * if possible
   * 
   * @param columns
   */
  private void redrawGrid(int columns) {
    grid.getChildren().clear();
    int column = 0;
    int row = 1;
    try {
      for (int i = 0; i < recipes.size(); i++) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/home/GridItem.fxml"));
        VBox vbox = fxmlLoader.load();

        GridItemController itemController = fxmlLoader.getController();
        try {
          itemController.injectNavigator(getNavigator());
        } catch (Exception e) {
          System.out.println(e);
        }
        itemController.setData(recipes.get(i));

        if (column == columns) {
          column = 0;
          row++;
        }

        grid.add(vbox, column++, row);

        GridPane.setMargin(vbox, new Insets(10));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Redraw the grid. Warning: Super slow because of so many FXML loaders; avoid
   * if possible
   * 
   * @param columns
   */
  private void redrawGridDishList(int columns, ArrayList<ArrayList<ArrayList<RecipeInterface>>> recipes) {
    grid.getChildren().clear();
    int column = 0;
    int row = 1;

    try {
      Weekday[] weekdays = Weekday.values();
      MealTime[] mealTimes = MealTime.values();

      for (int i = 0; i < recipes.size(); i++) {
        ArrayList<ArrayList<RecipeInterface>> level2List = recipes.get(i);

        for (int j = 0; j < level2List.size(); j++) {
          ArrayList<RecipeInterface> level3List = level2List.get(j);

          for (int k = 0; k < level3List.size(); k++) {
            RecipeInterface recipe = level3List.get(k);
            Recipe r = null;
            if (recipe instanceof Recipe) {
              r = (Recipe) recipe;
            } else {
              throw new IllegalArgumentException("RecipeInterface not convertable to Recipe");
            }
            if (k == 0) {
              FXMLLoader fxmlLoader = new FXMLLoader();
              fxmlLoader.setLocation(getClass().getResource("/home/GridItemDishList.fxml"));
              VBox vbox = fxmlLoader.load();
              vbox.setUserData(weekdays[i]);

              GridItemControllerDishList itemController = fxmlLoader.getController();
              try {
                itemController.injectNavigator(getNavigator());
              } catch (Exception e) {
                System.out.println(e);
              }
              itemController.setTextData(weekdays[i].toString() + ", " + mealTimes[j]
                  .toString() + ":");

              column = 0;
              row++;

              grid.add(vbox, column++, row);

              GridPane.setMargin(vbox, new Insets(10));
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/home/GridItem.fxml"));
            VBox vbox = fxmlLoader.load();

            GridItemController itemController = fxmlLoader.getController();
            try {
              itemController.injectNavigator(getNavigator());
            } catch (Exception e) {
              System.out.println(e);
            }
            itemController.setData(r);

            if (column == columns) {
              column = 0;
              row++;
            }

            grid.add(vbox, column++, row);

            GridPane.setMargin(vbox, new Insets(10));
          }

        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Redraw the grid with the existing data for better performance
   * 
   * @param columns The number of columns to display
   */
  private void redrawGridWithGridData(int columns) {
    // Save the current children of the grid in a list
    ArrayList<Node> gridItems = new ArrayList<>(grid.getChildren());

    // Clear the grid
    grid.getChildren().clear();

    int column = 0;
    int row = 1;

    for (Node node : gridItems) {
      if (column == columns) {
        column = 0;
        row++;
      }

      // Add the node back to the grid at the new position
      grid.add(node, column++, row);

      // Set the margin
      GridPane.setMargin(node, new Insets(10));
    }
  }

  /**
   * Redraw the grid with the existing data for better performance
   * 
   * @param columns The number of columns to display
   */
  private void redrawGridWithGridDataDishList(int columns) {
    // Save the current children of the grid in a list
    ArrayList<Node> gridItems = new ArrayList<>(grid.getChildren());

    // Clear the grid
    grid.getChildren().clear();

    int column = 0;
    int row = 1;

    Node previousNode = null;
    for (Node node : gridItems) {
      System.out.println(node.getUserData());
      if (
      // Go to the next line if the row is full
      column == columns ||
      // Go to the next line if the previous node was a recipe and the current node is
      // a Weekday
          (previousNode != null && previousNode.getUserData() == null && node.getUserData() != null)) {
        column = 0;
        row++;
      }

      // Add the node back to the grid at the new position
      grid.add(node, column++, row);

      // Set the margin
      GridPane.setMargin(node, new Insets(10));
      previousNode = node;
    }
  }

  public void addTagToGrid(Tag tag) {
    if (addedTagIds.contains(tag.getId())) {
      return;
    }
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/home/FilterItem.fxml"));
      HBox tagItem = fxmlLoader.load();

      FilterItemController tagItemController = fxmlLoader.getController();
      // Set the user data so we can access the tag from the node when removing the
      // tag
      tagItem.setUserData(tag);
      tagItemController.setData(tag);
      tagItemController.injectNavigator(getNavigator());

      tagsGrid.add(tagItem, tagsGrid.getChildren().size() % TAG_GRID_COLUMN_COUNT,
          tagsGrid.getChildren().size() / TAG_GRID_COLUMN_COUNT);
      GridPane.setMargin(tagItem, new Insets(2));
      // only add the tag id if it was successfully added to the grid
      addedTagIds.add(tag.getId());
      search(null, null, searchField.getText());
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (!selTagsLabel.isVisible()) {
      selTagsLabel.setVisible(true);
    }
  }

  public void removeTagFromGrid(Tag tag) {
    for (Node node : tagsGrid.getChildren()) {
      Tag currentTag = (Tag) node.getUserData();
      if (currentTag.getId() == tag.getId()) {
        tagsGrid.getChildren().remove(node);
        break;
      }
    }
    try {
      redrawTagGridFromGridData();
      addedTagIds.removeIf(id -> id == tag.getId());
      search(null, null, searchField.getText());
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (addedTagIds.isEmpty()) {
      selTagsLabel.setVisible(false);
    }
  }

  private void redrawTagGridFromGridData() {
    // Save the current children of the grid in a list
    ArrayList<Node> tagItems = new ArrayList<>(tagsGrid.getChildren());

    // Clear the grid
    tagsGrid.getChildren().clear();

    for (Node node : tagItems) {
      // Add the node back to the grid
      tagsGrid.add(node, tagsGrid.getChildren().size() % TAG_GRID_COLUMN_COUNT,
          tagsGrid.getChildren().size() / TAG_GRID_COLUMN_COUNT);
    }
  }

  public void addIngredientToGrid(Ingredient ingredient) {
    if (addedIngredientIds.contains(ingredient.getId())) {
      return;
    }
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/home/FilterItem.fxml"));
      HBox filterItem = fxmlLoader.load();

      FilterItemController controller = fxmlLoader.getController();
      // Set the user data so we can access the tag from the node when removing the
      // tag
      filterItem.setUserData(ingredient);
      controller.setData(ingredient);
      controller.injectNavigator(getNavigator());

      ingredientsGrid.add(filterItem, ingredientsGrid.getChildren().size() % INGREDIENT_GRID_COLUMN_COUNT,
          ingredientsGrid.getChildren().size() / INGREDIENT_GRID_COLUMN_COUNT);
      GridPane.setMargin(filterItem, new Insets(2));
      // only add the tag id if it was successfully added to the grid
      addedIngredientIds.add(ingredient.getId());
      // Refresh the recipes
      search(null, null, searchField.getText());
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (!selIngredientsLabel.isVisible()) {
      selIngredientsLabel.setVisible(true);
    }
  }

  public void removeIngredientFromGrid(Ingredient ingredient) {
    for (Node node : ingredientsGrid.getChildren()) {
      Ingredient currentIngredient = (Ingredient) node.getUserData();
      if (currentIngredient.getId() == ingredient.getId()) {
        ingredientsGrid.getChildren().remove(node);
        break;
      }
    }
    try {
      redrawIngredientsGridFromGridData();
      addedIngredientIds.removeIf(id -> id == ingredient.getId());
      search(null, null, searchField.getText());
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (addedIngredientIds.isEmpty()) {
      selIngredientsLabel.setVisible(false);
    }
  }

  private void redrawIngredientsGridFromGridData() {
    // Save the current children of the grid in a list
    ArrayList<Node> ingredientItems = new ArrayList<>(ingredientsGrid.getChildren());

    // Clear the grid
    ingredientsGrid.getChildren().clear();

    for (Node node : ingredientItems) {
      // Add the node back to the grid
      ingredientsGrid.add(node, ingredientsGrid.getChildren().size() % INGREDIENT_GRID_COLUMN_COUNT,
          ingredientsGrid.getChildren().size() / INGREDIENT_GRID_COLUMN_COUNT);
    }
  }

  @FXML
  public void btOpenProfile() {
    getNavigator().pushAsPopup(Route.PROFILE, 500);
  }

  @FXML
  public void btFilterSearch() {
    if (filterPopOver.isShowing()) {
      filterPopOver.hide();
      return;
    }
    filterPopOver.show(filterButton);
  }

  @FXML
  public void showAllRecipes() {
    switchTab(Tab.ALL_RECIPES);
  }

  @FXML
  public void showMyRecipes() {
    switchTab(Tab.MY_RECIPES);
  }

  @FXML
  public void showFavorites() {
    switchTab(Tab.FAVORITES);
  }

  @FXML
  public void showDishList() {
    switchTab(Tab.DISH_LIST);
  }

  @FXML
  public void btAddRecipe() {
    getNavigator().push(Route.ADD_RECIPE);
  }

  @FXML
  public void btAddDensity() {
    getNavigator().pushAsPopup( Route.ADD_DENSITY,300);
  }

  @FXML
  public void btShareRecipe() {
    getNavigator().pushAsPopup(Route.SHARE_RECIPE, 300);
  }

  @FXML
  public void btLogout() {
    Auth.logout();
    getNavigator().offAll(Route.LOGIN);
  }

  @FXML
  void btTutorial(ActionEvent event) {
    getNavigator().push(Route.TUTORIAL);
  }

  @FXML
  public void btOpenAdmin() {
    getNavigator().push(Route.ADMIN);
  }

  @FXML
  public void btWeeklyDinner() {
    getNavigator().push(Route.WEEKLY_DINNER);
  }

  // Tutorial
  @FXML
  public void btFridge() {
    getNavigator().push(Route.FRIDGE);
  }

  @FXML
  public void btShoppingList() {
    getNavigator().push(Route.SHOPPING_LIST);
  }

  @FXML
  public void btShowMessages(ActionEvent event) {
    try {
      ShowMessagesController controller = (ShowMessagesController) getNavigator().pushAsPopup(Route.SHOW_MESSAGES,480);
      controller.setData();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

}
