package cookbook.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SearchFunctionController extends RouteController implements Initializable {
  
  @FXML
  private TextField search;

  @FXML
  private ListView<keyWords> list;

  @FXML
  private ImageView showFunction;

  private enum keyWords {
    Recipe(),
    Add_Recipe(),
    Add_User(),
    Modify_User(),
    My_Recipes(),
    All_Recipes(),
    Favorites(),
    Share_Recipe(),
    Show_Messages(),
    Weekly_Dinner_List(),
    Fridge(),
    Shopping_List(),
    Administration(),
    Search_For_Recipe(),
    Login(),
    Tags(),
    Ingredients(),
    Delete_User();

    // private final String path;

    /*private Image(String path) {
      this.path = path.getValue();
    }*/
  }

  private ArrayList<keyWords> examples;

  @FXML
  private void handleMouseClick(MouseEvent event) {
    if (list.getSelectionModel().getSelectedItem() != null) {
    } else {
      showFunction.setImage(null);
    }
  }

  @FXML
  private void handleOnKeyReleased(KeyEvent event) {
    if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
        || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
        || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.SPACE
        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
      return;
    }

    String enteredText = search.getText();

    examples.clear();

    if (enteredText.equals("") || enteredText == null) {
      for (keyWords words : keyWords.values()) {
        examples.add(words);
      }
      return;
    }
    
    for (keyWords word : keyWords.values()) {
      if (word.name().toLowerCase().contains(enteredText) || word.name().contains(enteredText)) {
        examples.add(word);
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    /*for (keyWords word : keyWords.values()) {
      examples.add(word);
    }*/
  }
}
