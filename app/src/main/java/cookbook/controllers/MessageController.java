package cookbook.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cookbook.db.Message;
import cookbook.db.Recipe;
import cookbook.db.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class MessageController extends RouteController implements Initializable {
  private User user;
  private Recipe recipe;

  @FXML
  private Label sender;

  @FXML
  private TextArea sentMessage;

  @FXML
  private Label sentRecipe;

  public void setData(Message message) {
    try {
      this.user = User.fromDb(message.getSenderId());
      sender.setText(user.getDisplayName());
      this.recipe = Recipe.fromDB(message.getRecipeId());
      sentRecipe.setText(recipe.getName());
      sentMessage.setText(message.getMessageText());
    } catch (Exception e) {
      showError(e);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      sentMessage.setWrapText(true); // Enable text wrapping
      sentMessage.setEditable(false);

      // Bind the height of the TextArea to its content height
      sentMessage.textProperty().addListener((obs, oldText, newText) -> {
        int numberOfLines = newText.split("\n", -1).length;

        // Change this if you change the font size on the TextArea
        final int lineHeight = 21;
        // Padding of the TextArea
        final int padding = 12;
        
        sentMessage.setMinHeight(numberOfLines * lineHeight + padding);
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
