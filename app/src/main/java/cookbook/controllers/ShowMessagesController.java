package cookbook.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import cookbook.auth.Auth;
import cookbook.db.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ShowMessagesController extends RouteController implements Initializable {

  @FXML
  private VBox messages;

  public void loadMessage(Message message) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/Message.fxml"));
      HBox content = fxmlLoader.load();
      content.setUserData(message);

      MessageController controller = fxmlLoader.getController();

      controller.injectNavigator(getNavigator());
      controller.setData(message);

      messages.getChildren().add(content);
    } catch (Exception e) {
      showError(e);
    }
  }

  public void setData() {
    try {
      ArrayList<Message> allMessages = cookbook.db.Message.getAllTo(Auth.currentUser().getId());
      for (var message : allMessages) {
        loadMessage(message);
      }

    } catch (Exception e) {
      showError(e);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
}
