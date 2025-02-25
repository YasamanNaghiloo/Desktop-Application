package cookbook.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GridItemControllerDishList extends RouteController {

  @FXML
  private Label nameLabel;

  public void setTextData(String text) {
    nameLabel.setText(text);
  }

}
