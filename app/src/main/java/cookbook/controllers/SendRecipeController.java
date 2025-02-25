package cookbook.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cookbook.Message;
import cookbook.db.Recipe;
import cookbook.db.User;
import cookbook.navigator.Route;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class SendRecipeController extends RouteController implements Initializable {
  private Message message = new Message();

  @FXML
  private ComboBox<User> receiver;

  @FXML
  private Button sendButton;

  @FXML
  private TextArea sendMessage;

  @FXML
  private ComboBox<Recipe> sendRecipe;

  private ObservableList<Recipe> recipes;

  private ObservableList<User> users;

  @FXML
  private void send(ActionEvent event) {
    try {
      message.sendMessage(sendRecipe.getSelectionModel().getSelectedItem().getId(),
          receiver.getSelectionModel().getSelectedItem().getId(), sendMessage.getText());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    getNavigator().closeRoutePopup(Route.SHARE_RECIPE);
  }

  @FXML
  private void handleOnKeyReleasedUser(KeyEvent event) {
    if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
        || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
        || event.getCode() == KeyCode.HOME
        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
      return;
    }

    String enteredText = receiver.getEditor().getText();

    if (enteredText == null || enteredText.isEmpty()) {
      receiver.hide();
      return;
    }
    try {
      users.setAll(User.getAllLike(enteredText));
    } catch (Exception e) {
      users.setAll(FXCollections.observableArrayList());
      showError(e);
    }
    if (users.isEmpty()) {
      receiver.hide();
    } else {
      receiver.show();
    }
  }

  @FXML
  private void handleOnKeyReleasedRecipe(KeyEvent event) {
    if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
        || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
        || event.getCode() == KeyCode.HOME
        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
      return;
    }

    String enteredText = sendRecipe.getEditor().getText();

    if (enteredText == null || enteredText.isEmpty()) {
      sendRecipe.hide();
      return;
    }
    recipes.setAll(Recipe.getAllLike(enteredText));
    if (recipes.isEmpty()) {
      sendRecipe.hide();
    } else {
      sendRecipe.show();
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      receiver.setEditable(true);
      receiver.getEditor().setOnKeyReleased(this::handleOnKeyReleasedUser);
      users = FXCollections.observableArrayList(User.getAll());
      receiver.setItems(users);
      receiver.setStyle("-fx-font-family: 'Lucida Bright';");
      receiver.setStyle("-fx-background-color: '#FA9D83';");
      receiver.setConverter(new StringConverter<User>() {
        @Override
        public String toString(User user) {
          return user != null ? user.getDisplayName() : "";
        }

        @Override
        public User fromString(String string) {
          if (string == null || string.isEmpty()) {
            try {
              users = FXCollections.observableArrayList(User.getAll());
            } catch (Exception e) {
              users = FXCollections.observableArrayList();
            }
            receiver.setItems(users);
            return null;
          }
          // return the first user that matches the entered text
          try {
            return User.getAllLike(string).stream().findFirst().orElse(null);
          } catch (Exception e) {
            return null;
          }
        }
      });
      receiver.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
        receiver.setValue(null);
      });

      sendRecipe.setEditable(true);
      sendRecipe.getEditor().setOnKeyReleased(this::handleOnKeyReleasedRecipe);
      recipes = FXCollections.observableArrayList(Recipe.getAll());
      sendRecipe.setItems(recipes);
      sendRecipe.setStyle("-fx-font-family: 'Lucida Bright';");
      sendRecipe.setStyle("-fx-background-color: '#FA9D83';");
      sendRecipe.setConverter(new StringConverter<Recipe>() {
        @Override
        public String toString(Recipe recipe) {
          return recipe != null ? recipe.getName() : "";
        }

        @Override
        public Recipe fromString(String string) {
          if (string == null || string.isEmpty()) {
            recipes = FXCollections.observableArrayList(Recipe.getAll());
            sendRecipe.setItems(recipes);
            return null;
          }
          // return the first recipe that matches the entered text
          return Recipe.getAllLike(string).stream().findFirst().orElse(null);
        }
      });
      // Set the value to null when the user types in the ComboBox
      // This fixes the issue where JavaFX would replace the text with the
      // previously selected item after selecting an item and then deleting
      // the text to search for another item
      sendRecipe.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
        sendRecipe.setValue(null);
      });
    } catch (Exception e) {
      showError(e);
    }
  }
}
