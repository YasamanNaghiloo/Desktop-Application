package cookbook.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import cookbook.auth.Auth;
import cookbook.server.Server;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ProfileController extends RouteController implements Initializable {
  @FXML
  private ImageView profileImage;

  @FXML
  private TextField usernameField;

  @FXML
  private TextField displayNameField;

  @FXML
  private PasswordField passwordField;

  File newProfilePicture;

  String initialUsername = Auth.currentUser().getUsername();
  String initialDisplayName = Auth.currentUser().getDisplayName();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      if (Auth.currentUser().getImagePath() == null) {
        Image defaultProfilePicture = new Image(getClass().getResourceAsStream("/DefaultProfilePicture.png"));
        profileImage.setImage(defaultProfilePicture);
      } else {
        String url = Server.pathToUrl(Auth.currentUser().getImagePath());
        System.out.println(url);
        Image userImage = new Image(url);
        profileImage.setImage(userImage);

        usernameField.setText(Auth.currentUser().getUsername());
        displayNameField.setText(Auth.currentUser().getDisplayName());
      }
    } catch (Exception e) {
      showError(e);
    }
  }

  @FXML
  public void btSelectProfilePicture() {
    // Create a FileChooser
    FileChooser fileChooser = new FileChooser();

    // Set title for the FileChooser dialog
    fileChooser.setTitle("Select an image");

    // Only allow images to be selected
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg",
        "*.gif");
    fileChooser.getExtensionFilters().add(extFilter);

    // Show open dialog
    File selectedFile = fileChooser.showOpenDialog(getNavigator().getPrimaryStage());

    // Process the selected file
    if (selectedFile != null) {
      this.newProfilePicture = selectedFile;

      // Create an Image object from the selected file
      Image image = new Image(this.newProfilePicture.toURI().toString());

      // Set the Image object to the ImageView
      profileImage.setImage(image);
    }
  }

  @FXML
  public void btSave() {
    try {
      HomeController homeController = (HomeController) getNavigator().getCurrentRoute().getController();

      if (!passwordField.getText().isEmpty()) {
        Auth.currentUser().setPassword(passwordField.getText());
      }

      if (initialUsername != usernameField.getText() && !usernameField.getText().equals("")) {
        Auth.currentUser().setUsername(usernameField.getText());
      }

      if (initialDisplayName != displayNameField.getText() && !displayNameField.getText().equals("")) {
        Auth.currentUser().setDisplayName(displayNameField.getText());

        homeController.updateDisplayName();
      }

      if (newProfilePicture != null) {
        String path = Server.uploadFile(newProfilePicture);
        Auth.currentUser().setImagePath(path);
        
        homeController.updateProfilePicture();
      }

      getNavigator().showDialog("Success", "Profile updated successfully.");
    } catch (Exception e) {
      showError(e);
    }
  }
}
