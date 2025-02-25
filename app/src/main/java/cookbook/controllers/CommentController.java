package cookbook.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import cookbook.auth.Auth;
import cookbook.db.Comment;
import cookbook.db.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class CommentController extends RouteController implements Initializable {
  private int id;
  private User op;
  
  @FXML
  private MenuButton menu;
  
  @FXML
  private Label username;
  
  @FXML
  private TextArea content;

  public void setData(int id, int userId, String content) {
    try {
      this.id = id;
      this.op = User.fromDb(userId);
      this.username.setText(op.getDisplayName());
      this.content.setText(content);
  
      if (Auth.currentUser().getId() == op.getId() || Auth.currentUser().isAdmin()) {
        MenuItem edit = new MenuItem("Edit");
        edit.setStyle("-fx-font-family: 'Lucida Bright';");
        edit.setOnAction(e -> editComment());
        
        MenuItem delete = new MenuItem("Delete");
        delete.setStyle("-fx-font-family: 'Lucida Bright';");
        delete.setOnAction(e -> deleteComment());
  
        menu.getItems().addAll(edit, delete);
      }
    } catch (Exception e) {
      showError(e);
    }
  }

  @FXML
  public void editComment() {
    content.setEditable(true);
    content.requestFocus();
    
    // set editable to false after pressing enter but not when pressing shift + enter
    content.setOnKeyPressed(e -> {
      // ENTER
      if (e.getCode().getName().equals("Enter")) {
        // SHIFT
        if (!e.isShiftDown()) {
          content.setEditable(false);
          // remove the last newline character from the content
          content.setText(content.getText().substring(0, content.getText().length() - 1));
          // Get the Recipe View Controller
          RecipeViewController controller = (RecipeViewController) getNavigator().getCurrentRoute().getController();
          try {
            // Update the comment in the database
            Comment.fromDB(this.id).update(content.getText());
          } catch (Exception ex) {
            controller.showError(ex);
          }
          // Remove the key event listener
          content.setOnKeyPressed(null);
        } else {
          // append a newline character to the content when pressing shift + enter
          content.appendText("\n");
        }
      }
    });
  }

  @FXML
  public void deleteComment() {
    RecipeViewController controller = (RecipeViewController) getNavigator().getCurrentRoute().getController();
    controller.deleteComment(id);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      content.setWrapText(true); // Enable text wrapping

      // Bind the height of the TextArea to its content height
      content.textProperty().addListener((obs, oldText, newText) -> {
        int numberOfLines = newText.split("\n", -1).length;

        // Change this if you change the font size on the TextArea
        final int lineHeight = 21;
        // Padding of the TextArea
        final int padding = 12;
        
        content.setMinHeight(numberOfLines * lineHeight + padding);
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
