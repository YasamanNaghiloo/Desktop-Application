package cookbook.controllers;

import cookbook.db.Ingredient;
import cookbook.db.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * JavaFX controller for the filter pop over.
 */
public class FilterController extends RouteController {
  @FXML
  private ComboBox<Tag> tagComboBox;

  @FXML
  private ComboBox<Ingredient> ingredientsComboBox;

  ObservableList<Tag> tagList;
  ObservableList<Ingredient> ingredientList;

  @FXML
  private void initialize() {
    tagComboBox.setEditable(true);
    tagComboBox.getEditor().setOnKeyReleased(this::handleOnKeyReleasedTag);
    tagList = FXCollections.observableArrayList(Tag.getAll());
    tagComboBox.setItems(tagList);
    tagComboBox.setConverter(new StringConverter<Tag>() {
      @Override
      public String toString(Tag tag) {
        return tag != null ? tag.getName() : "";
      }

      @Override
      public Tag fromString(String string) {
        System.out.println("fromString: " + string);
        if (string == null || string.isEmpty()) {
          tagList = FXCollections.observableArrayList(Tag.getAll());
          tagComboBox.setItems(tagList);
          return null;
        }
        // return the first tag that matches the entered text
        return Tag.getAllLike(string).stream().findFirst().orElse(null);
      }
    });
    tagComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      System.out.println("Value changed");
      if (newVal != null) {
        // prevent accidental selection of the same tag
        if (oldVal != null && oldVal.getId() == newVal.getId()) {
          return;
        }
        System.out.println("Selected Tag: " + newVal.getName());

        HomeController homeController = (HomeController) getNavigator().getCurrentRoute().getController();
        if (!homeController.isFilterPopOverShowing()) {
          System.out.println("Filter Pop Over isn't showing anymore, skipping add call to prevent accidental 'add'");
          return;
        }
        homeController.addTagToGrid(newVal);
      }
    });

    // Set the value to null when the user types in the ComboBox
    // This fixes the issue where JavaFX would replace the text with the
    // previously selected item after selecting an item and then deleting
    // the text to search for another item
    tagComboBox.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      tagComboBox.setValue(null);
    });

    // Ingredient ComboBox
    ingredientsComboBox.setEditable(true);
    ingredientsComboBox.getEditor().setOnKeyReleased(this::handleOnKeyReleasedIngredient);
    ingredientList = FXCollections.observableArrayList(Ingredient.getAll());
    ingredientsComboBox.setItems(ingredientList);
    ingredientsComboBox.setConverter(new StringConverter<Ingredient>() {
      @Override
      public String toString(Ingredient ingredient) {
        return ingredient != null ? ingredient.getName() : "";
      }

      @Override
      public Ingredient fromString(String string) {
        System.out.println("fromString: " + string);
        if (string == null || string.isEmpty()) {
          ingredientList = FXCollections.observableArrayList(Ingredient.getAll());
          ingredientsComboBox.setItems(ingredientList);
          return null;
        }
        // return the first tag that matches the entered text
        return Ingredient.getAllLike(string).stream().findFirst().orElse(null);
      }
    });
    ingredientsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        // prevent accidental selection of the same tag
        if (oldVal != null && oldVal.getId() == newVal.getId()) {
          return;
        }
        System.out.println("Selected Ingredient: " + newVal.getName());

        HomeController homeController = (HomeController) getNavigator().getCurrentRoute().getController();
        if (!homeController.isFilterPopOverShowing()) {
          System.out.println("Filter Pop Over isn't showing anymore, skipping add call to prevent accidental 'add'");
          return;
        }
        homeController.addIngredientToGrid(newVal);
      }
    });
    // Set the value to null when the user types in the ComboBox
    // This fixes the issue where JavaFX would replace the text with the
    // previously selected item after selecting an item and then deleting
    // the text to search for another item
    ingredientsComboBox.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      ingredientsComboBox.setValue(null);
    });
  }

  private void handleOnKeyReleasedTag(KeyEvent keyEvent) {
    System.out.println("Key released: " + keyEvent.getCode());
    if (ignoreKeyCode(keyEvent.getCode())) {
      return;
    }

    String enteredText = tagComboBox.getEditor().getText();

    if (enteredText == null || enteredText.isEmpty()) {
      tagComboBox.hide();
      return;
    }
    tagList.setAll(Tag.getAllLike(enteredText));
    if (tagList.isEmpty()) {
      tagComboBox.hide();
    } else {
      tagComboBox.show();
    }
  }

  private void handleOnKeyReleasedIngredient(KeyEvent keyEvent) {
    if (ignoreKeyCode(keyEvent.getCode())) {
      return;
    }

    String enteredText = ingredientsComboBox.getEditor().getText();

    if (enteredText == null || enteredText.isEmpty()) {
      ingredientsComboBox.hide();
      return;
    }

    ingredientList.setAll(Ingredient.getAllLike(enteredText));

    if (ingredientList.isEmpty()) {
      ingredientsComboBox.hide();
    } else {
      ingredientsComboBox.show();
    }
  }

  private boolean ignoreKeyCode(KeyCode keycode) {
    return keycode == KeyCode.UP || keycode == KeyCode.DOWN
        || keycode == KeyCode.RIGHT || keycode == KeyCode.LEFT
        || keycode == KeyCode.HOME
        || keycode == KeyCode.END || keycode == KeyCode.TAB;
  }
}
