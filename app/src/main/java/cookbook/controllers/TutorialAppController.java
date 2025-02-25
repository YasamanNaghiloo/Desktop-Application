package cookbook.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class TutorialAppController extends RouteController implements Initializable {

  @FXML
  private Button next;

  @FXML
  private Button back;

  @FXML
  private ImageView imageView;

  @FXML
  private Text instructionText;

  @FXML
  private TextField search;

  private enum keyWords {
    Recipe("/tutorial/RecipeView.png"), View_Recipe("/tutorial/RecipeView.png"),
    Add_Recipe("/tutorial/AddRecipe.png"), Add("/tutorial/AddRecipe.png"),
    Add_User("/tutorial/AddUser.png"), User("/tutorial/AddUser.png"),
    Profile("/tutorial/ModifyProfileByUser.png"), Update_User("/tutorial/Administration2.png"),
    Modify_User("/tutorial/ModifyUser.png"), Modify("/tutorial/ModifyUser.png"),
    Share("/tutorial/ShareRecipe.png"), Share_Recipe("/tutorial/ShareRecipe.png"),
    Density("/tutorial/AddDensity.png"), Comment("/tutorial/Write comment.png"),
    Write("/tutorial/Write comment.png"), Edit("/tutorial/EditComment.png"),
    Edit_Comment("/tutorial/EditComment.png"), Favorite("/tutorial/Favourites.png"),
    Favourite("/tutorial/Favourites.png"), Scale("/tutorial/scale.png"),
    Show_Messages("/tutorial/ShowMessages.png"), Message("/tutorial/ShowMessages.png"),
    Add_Weekly_Dinner("/tutorial/AddToWeekly.png"), Weekly_Dinner("/tutorial/PickDay.png"),
    Weekly_Dinner_Menu("/tutorial/PickDay.png"), Weekly_Dinner_List("/tutorial/WeeklyDinner.png"),
    Weekly("/tutorial/WeeklyDinner.png"), Menu_Bar("/tutorial/HomeScreenTabs.png"),
    Tab("/tutorial/HomeScreenTabs.png"), Bar("/tutorial/HomeScreenTabs.png"),
    Fridge("/tutorial/Fridge.png"), Shopping_List("/tutorial/ShoppingList.png"),
    Shopping("/tutorial/ShoppingList.png"), Admin("/tutorial/Administration1.png"),
    Search_For_Recipe("/tutorial/TagAndIngredientsSearch.png"), Tag("/tutorial/TagAndIngredientsSearch.png"),
    Ingredient("/tutorial/TagAndIngredientsSearch.png"), Login("/tutorial/Login.png"),
    Delete_User("/tutorial/Administration2.png"), Delete("/tutorial/Administration2.png"),
    Home("/tutorial/HomePage1-01.png"), Search("/tutorial/ProfileAndSearch.png");

    private final String path;

    private keyWords(String path) {
      this.path = path;
    }
  }

  private int currentStep = 0;

  @FXML
  private void handleNext() {
    if (currentStep < 26) {
      currentStep++;
      updateSlide();
    }

  }

  /**
   * Here must a condition must be added aswell in order to controll currentStep.
   * 
   */
  @FXML
  private void handleBack() {
    if (currentStep > 0) {
      currentStep--;
      updateSlide();
    }
  }

  @FXML
  private void handleOnAction(ActionEvent event) {
    if (search.getText().isEmpty()) {
      updateSlide();
      return;
    } else {
      for (keyWords word : keyWords.values()) {
        if (word.name().replace("_", " ").toLowerCase().equals(search.getText().toLowerCase())) {
          imageView.setImage(new Image(getClass().getResourceAsStream(word.path)));
        }
      }
    }
  }

  private void updateSlide() {
    switch (currentStep) {
      case 1:
        back.setVisible(false);
        instructionText.setText("Welcome to the cookbook app tutorial");
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/welcomeee.png")));
        break;
      case 2:
        back.setVisible(true);
        instructionText.setText("This is how you use buttons");
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/HomePage1-01.png")));
        break;
      case 3:
        String text = "Tab bar tutorials";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream(
            "/tutorial/HomeScreenTabs.png")));
        break;
      case 4:
        instructionText.setText("Profile and search tutorials");
        imageView.setImage(new Image(getClass().getResourceAsStream(
            "/tutorial/ProfileAndSearch.png")));
        break;
      case 5:
        text = "Search filters";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream(
            "/tutorial/TagAndIngredientsSearch.png")));
        break;
      case 6:
        text = "Profile view";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream(
            "/tutorial/ModifyProfileByUser.png")));
        break;
      case 7:
        text = "Add recipe";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/AddRecipe.png")));
        break;
      case 8:
        text = "Send recipes to your friend!";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/ShareRecipe.png")));
        break;
      case 9:
        text = "Weekly dinner list plan";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/WeeklyDinner.png")));
        break;
      case 10:
        text = "How to add them?";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/AddToWeekly.png")));
        break;
      case 11:
        text = "Pick a day to add it to your plan";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/PickDay.png")));
        break;
      case 12:
        text = "What do you have in your fridge?";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/Fridge.png")));
        break;
      case 13:
        text = "Time to shop for ingredients!";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/ShoppingList.png")));
        break;
      case 14:
        text = "Ding Ding! Got any messages?";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/ShowMessages.png")));
        break;
      case 15:
        text = "Care if you help us a bit?";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/AddDensity.png")));
        break;
      case 16:
        text = "Manage users by admin!";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream(
            "/tutorial/Administration.png")));
        break;
      case 17:
        text = "Add users to the beautiful cookbook :>";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream(
            "/tutorial/Administration1.png")));
        break;
      case 18:
        text = "Follow the steps to add user's information!";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/AddUser.png")));
        break;
      case 19:
        text = "Even modify or delete them!";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream(
            "/tutorial/Administration2.png")));
        break;
      case 20:
        text = "Modifying the user's information steps!";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/ModifyUser.png")));
        break;
      case 21:
        text = "By clicking on recipe at homescreen, the recipe will be shown";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/RecipeView.png")));
        break;
      case 22:
        text = "Adding a recipe to favourite list.";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/Favourites.png")));
        break;
      case 23:
        text = "How to scale a recipe.";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/Scale.png")));
        break;
      case 24:
        text = "Want to make a comment ?!";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/AddComment.png")));
        break;
      case 25:
        text = "Here is the instruction for a comment";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/WriteComment.png")));
        break;
      case 26:
        next.setVisible(true);
        back.setVisible(true);
        text = "More about comments";
        instructionText.setText(text);
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/EditComment.png")));
        break;
      default:
        next.setVisible(false);
        back.setVisible(true);
        instructionText.setText("You have completed the tutorial.");
        imageView.setImage(new Image(getClass().getResourceAsStream("/tutorial/.png")));
        break;
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    back.setVisible(false);
    next.setVisible(true);

  }
}
