package cookbook;

import cookbook.db.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


/** The class for searching a recipe by its tags.
 * 
 */
public class Searchbytag extends Application {

  private TextField tagName;
  private ArrayList<String> recipes = new ArrayList<String>();
  private boolean rowsAffected;
  private Stage stage;
  private CustomAlertMessage customAlertMessage;
  private HashMap<Integer, ArrayList<Integer>> recipeIds = new HashMap<>();
  private ImageHandler im;
  private Image tImage;




  public Searchbytag() {

  }

  /** Is the method in order to find all tag Ids.

   * @param tag is the given tag.
   * @return returns all tag Ids.
   */
  public ArrayList<Integer> searcH(String tag) {
    ArrayList<Integer> tagIds = new ArrayList<>();
    try {
      String query = "SELECT ct.tagId FROM cookbook.tag AS ct";
      query += " WHERE ct.tagName LIKE (?)";
      try (PreparedStatement fetch = DB.getInstance().getConnection().prepareStatement(query)) {
        fetch.setString(1, tag + "%");
        try (ResultSet resultSet = fetch.executeQuery()) {
          while (resultSet.next()) {
            int tagId = resultSet.getInt(1);
            tagIds.add(tagId);  
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return tagIds;
  }


  /** Is the method on order to return the recipeIds.

   * @param tagIds is the tagIDs.
   * @return it returns the recipes.
   */
  public ArrayList<String> searchForRecipeId(ArrayList<Integer> tagIds) {
    ArrayList<String> recipes = new ArrayList<String>();
    try {
      String query = "SELECT cr.* FROM cookbook.recipe AS cr";
      query += " JOIN cookbook.recipe_tag AS ctr ON cr.recipeId = ctr.recipeId";
      query += " WHERE  ctr.tagId = (?)";
      try (PreparedStatement query2 = DB.getInstance().getConnection().prepareStatement(query)) {
        for (int tagId : tagIds) {
          query2.setInt(1, tagId);
          try (ResultSet recipe = query2.executeQuery()) {
            while (recipe.next()) {
              int recId = recipe.getInt(1);
              getImageId("images", recId);
              int userId = recipe.getInt(2);
              String recName = recipe.getString(3);
              String recSh = recipe.getString(4);
              String recLo = recipe.getString(5);
              String recPo = recipe.getString(6);
              String all = "recipe Id: " + String.valueOf(recId) + "userId: ";
              all += String.valueOf(userId) + "recipe name: " + recName;
              all += "Short description: ";
              all += recSh + "Long description: " + recLo + "Portion" + recPo;
              recipes.add(all);
            }
          }
        }
      }
      this.rowsAffected = (recipes.size() > 0);
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }
    return recipes;
  }

  /**
   * A method in order to convert a String into an array of characters.

   * @param x is the string.
   * @return returns the array of characters.
   */
  public char [] stringConverter(String x) {
    int length = x.length();
    char [] chars = new char[length];
    for (int i = 0; i < length; i++) {
      chars[i] = x.charAt(i);
    }
    return chars;
  }

  /**
   * Is a method to check whether all the elements of an array are true or there are some false.

   * @param x is the given array.
   * @return returns a boolean.
   */
  public boolean allTrue(boolean [] x) {
    for (boolean t : x) {
      if (t != true) {
        return false;
      }
    }
    return true;
  }

  /** It's here just to test.

   * @param x is or are the tags.
   * @return it returns all the tags.
   */
  public ArrayList<String> tester(String x) {
    ArrayList<Integer> ids = searcH(x);
    ArrayList<String> rec = searchForRecipeId(ids);
    return rec;
  }

  /** We have this method in order to retrive the imageId.

   * @param table is the intended image table.
   * @param recipeId is the given recipeId.
   */
  public void getImageId(String table, int recipeId) {
    ArrayList<Integer> ids = new ArrayList<>();
    String q = "SELECT * FROM cookbook." + table + "WHERE recipeId = (?)";
    try (PreparedStatement st = DB.getInstance().getConnection().prepareStatement(q)) {
      st.setInt(1, recipeId);
      try (ResultSet rs = st.executeQuery()) {
        if (! rs.next()) {
          throw new IllegalArgumentException("The given recipe does not have any image.");
        }
        while (rs.next()) {
          int id = rs.getInt(1);
          ids.add(id);
        }
      }
      recipeIds.put(recipeId, ids);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException ee) {
      System.err.println(ee);
    }
  }

  /** is a class it gets an imageId and assigns it to tImage.

   * @param imageId is the ID.
   */
  public void getImage(int imageId) {
    try {
      this.tImage = im.displayImage("images", imageId);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  @Override
  public void start(Stage primaryStage) throws Exception {
    this.stage = primaryStage;
    this.customAlertMessage = new CustomAlertMessage();
    final Label tag = new Label("Please write the Tags");
    TextField inputTag = new TextField();
    inputTag.setPromptText("Separate each tag by a comma (,)");
    this.tagName = inputTag;
    inputTag.setStyle("-fx-background-radius: 5;");


    Button search = new Button("Search");
    search.setOnAction(event -> {
      try {
        starter();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Button resetButton = new Button("Back");
    resetButton.setOnAction(event -> {
      // Here must be implemented.
      System.out.println("Hello");
    });
    String reset = "-fx-background-radius: 50; -fx-font-size: 18px;";
    reset += " -fx-padding: 5px;";
    resetButton.setStyle(reset);
    resetButton.setText("Return");

    search.setStyle("-fx-background-radius: 30;");

    VBox root = new VBox(20);
    root.setPadding(new Insets(20));
    root.getChildren().addAll(tag, inputTag, search, resetButton);

    Scene scene = new Scene(root, 800, 500);
    stage.setScene(scene);
    stage.setTitle("Search for a new recipe by tags");
    stage.show();
  }


  /** The starter method which is used in search button.
   * 
   */
  public void starter() {

    VBox vbox = new VBox(20);
    vbox.setPadding(new Insets(20));
    try {
      if (!check(tagName)) {
        throw new IllegalArgumentException("Tags input can't be empty!");
      }
      if (!checkForTag(tagName)) {
        throw new IllegalArgumentException("The given input is not in right format!");
      }

      String tag = tagName.getText();
      final String[] tags = tag.split("\\s*,\\s*");
      GridPane root = new GridPane();
      root.setPadding(new Insets(20));
      root.setHgap(35);
      root.setVgap(35);
      int rowIndex = 0;
      if (tags.length < 0) {
        throw new IllegalArgumentException("NO INPUT !!!");
      }
      for (int i = 0; i < tags.length; i++) {
        ArrayList<Integer> ids = searcH(tags[i]);
        ArrayList<String> recipes = searchForRecipeId(ids);
        if (recipes.size() == 0) {
          throw new IllegalArgumentException("Nothing was found!!!");
        }
        Label ingType = new Label(tags[i]);
        ingType.setStyle("-fx-font-weight: bold;");
        root.add(ingType, 0, rowIndex++);
        for (int j = 0; j < recipes.size(); j++) {
          Label r = new Label(recipes.get(j));
          
          root.add(r, 0, rowIndex++);
        }
      }
      Scene scene = new Scene(root, 800, 500);
      stage.setScene(scene);
      stage.setTitle("The app message.");
      stage.show();
    } catch (IllegalArgumentException e) {
      displayErrorAlert(e.getMessage());
    } catch (Exception ee) {
      displayErrorAlert(ee.getMessage());
    }
  }

  private void displayErrorAlert(String errorMessage) {
    StackPane root = new StackPane();
    String image = "/479B9BC0-5C88-431D-8F4F-2C6AACFE0828_4_5005_c.jpeg";
    Image background = new Image(getClass().getResource(image).toExternalForm());
    ImageView backImageView = new ImageView(background);
    root.getChildren().add(backImageView);

    // Create the custom alert using the CustomAlertMessage class
    Alert alert = customAlertMessage.createCustomAlert(errorMessage);
    
    root.getChildren().add(alert.getDialogPane().getContent());

    Scene scene = new Scene(root, 400, 200);
    Stage alertStage = new Stage();
    alertStage.setScene(scene);
    alertStage.setTitle("Error");
    alertStage.show();

    // Close the alert stage after 3 seconds
    PauseTransition delay = new PauseTransition(Duration.seconds(3));
    delay.setOnFinished(event -> alertStage.close());
    delay.play();
  }



  /** This method is here for to check if the box is not empty or blank.

  * @param in is the input.
  * @return returns either true or false.
  */
  public boolean check(TextField in) {
    String tag = in.getText().trim();
    return !tag.isEmpty() && !tag.isBlank();
  }

  /** This is a method in order to check if the inputTag is written correctly.
   * * @param in are tags as a TextField.

  * @return returns either true or false.
  */
  public boolean checkForTag(TextField in) {
    String tag = in.getText().trim();
    String[] tags = tag.split("\\s*,\\s*");
    for (String part : tags) {
      if (part.isEmpty() || !part.matches("[a-zA-Z\\s]+")) {
        return false;
      }
    }
    return true; // All parts are valid
  }
}
