package cookbook.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import cookbook.Favorite;
import cookbook.TripleTuple;
import cookbook.auth.Auth;
import cookbook.db.Comment;
import cookbook.db.Recipe;
import cookbook.scaling.ScaleRecipe;
import cookbook.server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class RecipeViewController extends RouteController implements Initializable {
  private Recipe recipe;
  private Favorite favorite = new Favorite();

  @FXML
  private ImageView recipeImage;

  @FXML
  private VBox showAllComments;

  @FXML
  private CheckBox favourite;

  @FXML
  private TextArea showFullDescription;

  @FXML
  private TextArea showIngredients;

  @FXML
  private TextField showPortion;

  @FXML
  private TextField showRecipeName;

  @FXML
  private TextField showShortDescription;

  @FXML
  private TextField showTags;

  @FXML
  private TextField commentBox;

  @FXML
  private ImageView starImage;
  
  @FXML
  private AnchorPane view;

  Image star1;
  Image star2;

  /**
   * Method that adds a comment to the recipe.
   *
   * @param event.
   */
  @FXML
  public void postComment(ActionEvent event) {
    String comment = commentBox.getText();
    try {
      // Add a comment to the recipe
      var commentId = recipe.addComment(comment);

      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/home/Comment.fxml"));
      HBox content = fxmlLoader.load();
      // Set the comment id as the user data for the vbox so we can find it in the
      // future
      content.setUserData(commentId);

      CommentController controller = fxmlLoader.getController();

      controller.injectNavigator(getNavigator());
      controller.setData(commentId, Auth.currentUser().getId(), commentBox.getText());

      showAllComments.getChildren().add(content);
    } catch (Exception e) {
      showError(e);
    }
    commentBox.setText(null);
  }

  public void addToCommentSection(Comment comment) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/home/Comment.fxml"));
      HBox content = fxmlLoader.load();
      content.setUserData(comment.getId());

      CommentController controller = fxmlLoader.getController();

      controller.injectNavigator(getNavigator());
      controller.setData(comment.getId(), comment.getUserId(), comment.getContent());

      showAllComments.getChildren().add(content);
    } catch (Exception e) {
      showError(e);
    }
  }

  public void deleteComment(int commentId) {
    try {
      for (var node : showAllComments.getChildren()) {
        if (node.getUserData().equals(commentId)) {
          Comment.fromDB(commentId).delete();
          showAllComments.getChildren().remove(node);
          // Delete the comment from the database
          break;
        }
      }
    } catch (Exception e) {
      showError(e);
    }
  }

  /**
   * Method that adds a recipe to favorite if the checkbox is checked.
   *
   * @param event not used.
   */
  @FXML
  public void btAddFavourite(ActionEvent event) {
    try {
      if (favourite.isSelected()) {
        starImage.setImage(star2);
        favorite.addFavorite(this.recipe.getName(), Auth.currentUser().getId());
      } else if (!favourite.isSelected()) {
        starImage.setImage(star1);
        favorite.deleteFavorite(this.recipe.getName(), Auth.currentUser().getId());
      }
    } catch (Exception e) {
      showError(e);
    }
  }

  /**
   * Method that populate the ui with data based on the recipe handed from the
   * grid item controller.
   * 
   * @param recipe object that contains data about the recipe.
   */
  public void setData(Recipe recipe) {
    this.recipe = recipe;

    try {
      // Load the image if it exists
      if (recipe.getImagePath() != null && !recipe.getImagePath().isEmpty()) {
        this.recipeImage.setImage(new Image(Server.pathToUrl(recipe.getImagePath())));
      }
    } catch (Exception e) {
      showError(e);
    }
    showRecipeName.setText(this.recipe.getName());
    showPortion.setText("" + this.recipe.getPortion());
    showShortDescription.setText(this.recipe.getShortDescription());
    showFullDescription.setText(this.recipe.getFullDescription());
    showTags.setText("");
    for (var tag : recipe.getTags()) {
      showTags.appendText(tag.getName() + ", ");
    }

    for (var ingredient : recipe.getIngredients()) {
      showIngredients.appendText(ingredient.toString() + "\n");
    }

    // Populate the recipe view with comments
    ArrayList<cookbook.db.Comment> comments = cookbook.db.Comment.getAllFrom(this.recipe.getId());
    for (var c : comments) {
      addToCommentSection(c);
    }

    // Set the check box based on if the recipe is in favorites or not
    if (favorite.checkFavorite(this.recipe.getName(), Auth.currentUser().getId())) {
      starImage.setImage(star2);
      favourite.setSelected(true);
    } else {
      starImage.setImage(star1);
      favourite.setSelected(false);
    }
    System.out.println("Recipe ID: " + recipe.getId());
    System.out.println("Recipe Ingredients: " + recipe.getIngredients());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    star1 = new Image(getClass().getResourceAsStream("/star.png"));
    star2 = new Image(getClass().getResourceAsStream("/starSelected.png"));

    showPortion.setEditable(true);
    showPortion.setOnAction(event -> {
      ScaleRecipe scaler = new ScaleRecipe();
      int portion = 0;
      try {
        portion = Integer.valueOf(showPortion.getText());
      } catch (Exception e) {
        getNavigator().showDialog("portion changer", "Invalid portion value");
      }
      ArrayList<TripleTuple<String, Double, String>> tuples1 = 
          scaler.getScaleRecipe(recipe.getId(), portion);
        System.err.println(tuples1.size() + "SSIIISSZZEW");
      showIngredients.setText("");
      StringBuilder stringBuilder = new StringBuilder();
      for (TripleTuple<String, Double, String> tripleTuple : tuples1) {
        stringBuilder.append(tripleTuple.first + " "
            + tripleTuple.second + " " + tripleTuple.third + " " + "\n");
      }
      showIngredients.appendText(stringBuilder.toString());
      System.err.println("HÄÄÄÄÄR: " + stringBuilder.toString());
    });

  }
}
