package cookbook.controllers;

import cookbook.db.Ingredient;
import cookbook.db.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * It's not really a route but it behaves like one so we use it here anyway.
 */
public class FilterItemController extends RouteController {
  private Tag tag;
  private Ingredient ingredient;

  @FXML
  private Label nameLabel;

  public void setData(Tag tag) {
    this.tag = tag;
    nameLabel.setText(tag.getName());
  }

  public void setData(Ingredient ingredient) {
    this.ingredient = ingredient;
    nameLabel.setText(ingredient.getName());
  }

  @FXML
  public void deleteFilterItem() {
    // get the Home controller from the navigator (Always the current route when called from her) and
    // remove the tag from the home controller
    var homeController = (HomeController) getNavigator().getCurrentRoute().getController();
    if (tag != null) {
      homeController.removeTagFromGrid(tag);
    }
    if (ingredient != null) {
      homeController.removeIngredientFromGrid(ingredient);
    }
  }
}
