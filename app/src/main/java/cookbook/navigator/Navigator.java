package cookbook.navigator;

import cookbook.controllers.RouteController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.jna.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Navigator implements Initializable {
  // The primary stage of the application
  private Stage primaryStage;

  private ArrayList<HistoryItem> history = new ArrayList<>();

  @FXML
  private StackPane root;

  @FXML
  private HBox backButton;

  @FXML
  private Button minimizeButton;

  @FXML
  private Button closeButton;

  @FXML
  private Label titleLabel;

  @FXML
  private HBox titleBar;

  private double xOffset = 0;
  private double yOffset = 0;

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  /**
   * Get the primary stage
   * 
   * @return The primary stage
   */
  public Stage getPrimaryStage() {
    if (primaryStage == null) {
      System.err.println("[ERROR] Primary stage is not initialized yet. Please call setPrimaryStage() first.");
    }
    return primaryStage;
  }

  /**
   * Push a new route to the history
   * @param route The route to push
   * 
   * @return The controller of the pushed route
   */
  public RouteController push(Route route) {
    RouteController controller = null;	
    if (checkIfInHistory(route)) {
      System.out.println("[WARNING] Route /" + route.title + " is already in the history; skipping");
      return controller;
    }
    System.out.println("[INFO] Navigating to: " + getCurrentPath() + route.title);
    try {
      FXMLLoader loader1 = new FXMLLoader(getClass().getResource(route.path));
      Parent root1 = loader1.load();
      controller = loader1.getController();
      // Inject the navigator into the controller
      // So every Route can access the navigator
      controller.injectNavigator(this);
      var wrapper = new HistoryItem(root1, route, controller);
      history.add(wrapper);
      root.getChildren().clear();
      root.getChildren().add(wrapper.getItem());
      
      // Call the whenRouteLoaded method because the route is loaded
      controller.onRouteLoaded();
      // Make sure that the Back button is only visible when the history has more than one
      // item
      checkBackButton();
      titleLabel.setText(route.title);
    } catch (Exception e) {
      System.out.println("[ERROR] Unable to load Route: " + e);
    }
    return controller;
  }

  /**
   * Clear the history and push a new route
   * @param route The route to push
   */
  public RouteController offAll(Route route) {
    root.getChildren().clear();
    history.clear();
    return this.push(route);
  }

  /**
   * Go back to the previous route
   */
  public void goBack() {
    if (history.size() < 2) {
      System.out.println("[WARNING] Attempted to go back with no history");
      return;
    }
    // Call the pop callback if it exists
    if (getCurrentRoute().popCallback != null) {
      getCurrentRoute().popCallback.run();
    }
    history.remove(history.size() - 1);
    var wrapper = getCurrentRoute();
    root.getChildren().clear();
    root.getChildren().add(wrapper.getItem());
    checkBackButton();
    titleLabel.setText(wrapper.getRoute().title);
  }

  /**
   * Register a pop callback for the current route
   * 
   * @param callback The callback to register
   */
  public void registerPopCallback(Runnable callback) {
    if (history.size() == 0) {
      return;
    }
    var wrapper = history.get(history.size() - 1);
    wrapper.popCallback = callback;
  }

  public String getCurrentPath() {
    StringBuilder path = new StringBuilder("/");
    for (var item : history) {
      path.append(item.getRoute().title).append("/");
    }
    return path.toString();
  }

  /**
   * Check if the back button should be visible
   */
  public void checkBackButton() {
    if (history.size() > 1) {
      backButton.setVisible(true);
    } else {
      backButton.setVisible(false);
    }
  }

  /**
   * Check if a route is in the history
   * @param route The route to check
   * @return True if the route is in the history, false otherwise
   */
  private boolean checkIfInHistory(Route route) {
    for (var item : history) {
      if (item.getRoute().equals(route)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the current route
   * @return The current route
   */
  public HistoryItem getCurrentRoute() {
    return history.get(history.size() - 1);
  }

  /**
   * Get the route wrapper for a specific route
   * @param route 
   * @return
   */
  public HistoryItem getHistoryItem(Route route) {
    for (var item : history) {
      if (item.getRoute().equals(route)) {
        return item;
      }
    }
    return null;
  }

  /**
   * Button for going back
   */
  @FXML
  public void btBack() {
    System.out.println("[INFO] Going back");
    goBack();
  }

  /**
   * Button for minimizing the window
   */
  @FXML
  public void btMinimizeWindow() {
    primaryStage.setIconified(true);
  }
  
  /**
   * Button for closing the window
   */
  @FXML
  public void btCloseWindow() {
    System.exit(0);
  }

  // Event handler for mouse pressed event
  @FXML
  private void handleMousePressed(MouseEvent event) {
    if (!Platform.isWindows()) {
      return;
    }
    // No idea why, but the window is 7 pixels off
    yOffset = event.getSceneY() + 7;
    xOffset = event.getSceneX() + 7;
  }

  // Event handler for mouse dragged event
  @FXML
  private void handleMouseDragged(MouseEvent event) {
    if (!Platform.isWindows()) {
      return;
    }
    primaryStage.setY(event.getScreenY() - yOffset);
    primaryStage.setX(event.getScreenX() - xOffset);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    if (Platform.isWindows()) {
      // Default Window color for Windows windows
      titleBar.setStyle("-fx-background-color: #eff4f9;");
      // Show window controls
      minimizeButton.setVisible(true);
      closeButton.setVisible(true);
    }
  }

  /**
   * Show a dialog
   * 
   * @param title The title of the dialog
   * @param message The message of the dialog
   */
  public void showDialog(String title, String message) {
    VBox dialogBackdrop = new VBox();
    dialogBackdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
    dialogBackdrop.setAlignment(Pos.CENTER);

    VBox dialog = new VBox();
    dialog.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-spacing: 10px;");
    dialog.setAlignment(Pos.CENTER); // Center content vertically and horizontally
    dialog.setMaxWidth(300);

    var titleLabel = new Label(title);
    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
    titleLabel.setFont(new Font("Lucida Bright", 20));
    
    var messageLabel = new Label(message);
    titleLabel.setFont(new Font("Lucida Bright", 14));
    messageLabel.setWrapText(true);

    var closeButton = new Button("Ok");
    closeButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10px; -fx-effect: dropShadow(three-pass-box,rgba(0,0,0,0.1), 5.0, 0.0, 0.0, 5.0);");
    closeButton.setPrefWidth(100);
    closeButton.setOnAction(e -> {
      root.getChildren().remove(dialogBackdrop);
    });

    dialog.getChildren().addAll(titleLabel, messageLabel, closeButton);

    dialogBackdrop.getChildren().add(dialog);
    root.getChildren().add(dialogBackdrop);
  }

  public RouteController pushAsPopup(Route route, double maxWidth) {
    RouteController controller = null;
    Parent fxmlContent;
    VBox dialogBackdrop = new VBox();
    dialogBackdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
    dialogBackdrop.setAlignment(Pos.CENTER);

    StackPane dialog = new StackPane();
    dialog.setStyle("-fx-background-color: white; -fx-padding: 10px;");
    dialog.setMaxWidth(maxWidth);
    dialog.setAlignment(Pos.TOP_RIGHT);

    var closeButton = new Button("X");
    closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10px; -fx-effect: dropShadow(three-pass-box,rgba(0,0,0,0.1), 5.0, 0.0, 0.0, 5.0);");
    closeButton.setPrefWidth(40);
    closeButton.setOnAction(e -> {
      root.getChildren().remove(dialogBackdrop);
    });

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(route.path));
      fxmlContent = loader.load();
      controller = loader.getController();
      // Inject the navigator into the controller
      // So every Route can access the navigator
      controller.injectNavigator(this);
      // Call the whenRouteLoaded method because the route is loaded
      controller.onRouteLoaded();

      // Open the loaded FXML in a dialog
      dialog.getChildren().addAll(fxmlContent, closeButton);
      dialogBackdrop.getChildren().add(dialog);
      // Set the user data so we can find the dialog later
      dialogBackdrop.setUserData(route);

      this.root.getChildren().add(dialogBackdrop);
    } catch (Exception e) {
      System.out.println("[ERROR] Unable to load Route: " + e);
    }

    return controller;
  }

  public boolean closeRoutePopup(Route route) {
    // Start from the end of the children list because the dialog is most likely the last
    for (int i = root.getChildren().size() - 1; i >= 0; --i) {
      var node = root.getChildren().get(i);
      if (node.getUserData() == null || !(node.getUserData() instanceof Route)) {
        continue;
      }
      if (node.getUserData().equals(route)) {
        root.getChildren().remove(node);
        return true;
      }
    }
    return false;
  }
}
