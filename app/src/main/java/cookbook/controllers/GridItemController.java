package cookbook.controllers;

import cookbook.db.Recipe;
import cookbook.navigator.Route;
import cookbook.server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class GridItemController extends RouteController {
  private Recipe recipe;

  @FXML
  private Label nameLabel;

  @FXML
  private Tooltip hoverText;

  @FXML
  private ImageView img;

  @FXML
  private MenuItem weeklyDinner;

  public void setData(Recipe recipe) {
    this.recipe = recipe;
    weeklyDinner.setStyle("-fx-font-family: 'Lucida Bright'; -fx-font-size: 12 pt;");
    nameLabel.setText(recipe.getName());

    if (recipe.getImagePath() == null || recipe.getImagePath().isEmpty()) {
      return;
    }
    try {
      img.setImage(new Image(Server.pathToUrl(recipe.getImagePath())));
    } catch (Exception e) {
      // ignore the error because it likely means the image is not found on the server
      // and in this case we keep displaying the default image
    }
  }
  
  public void setTextData(String text) {
    nameLabel.setText(text);
  }

  @FXML
  private void mouseHover(MouseEvent event) {
    hoverText.setText(recipe.getShortDescription());
  }

  @FXML
  private void handleAddToWeeklyDinnerList(ActionEvent event) {
    try {
      WeeklyDinnerController controller = (WeeklyDinnerController) getNavigator().pushAsPopup(Route.ADD_TO_WEEKLY_DINNER, 300);
      controller.setData(recipe);
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public void btOnClick(MouseEvent event) {
    // Ignore all but primary mouse button
    if (event.getButton() != MouseButton.PRIMARY) {
      return;
    }
    System.out.println("Clicked on " + recipe.getName());
    try {
      RecipeViewController controller = (RecipeViewController) getNavigator().push(Route.RECIPE_VIEW);
      controller.setData(recipe);
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
