
package cookbook;

import cookbook.auth.Auth;
import cookbook.controllers.HomeController;
import cookbook.db.DB;
import cookbook.navigator.Navigator;
import cookbook.navigator.Route;
import cookbook.win_api.WinAPI;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.sun.jna.Platform;

/**
 * Main class for the application.
 */
public class App extends Application {

  public static final boolean DEBUG = false;

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Initialize the database
    if (DB.getInstance().getConnection() == null) {
      System.out.println("An error has occurred");
      return;
    }

    // Launch startup animation.
    
    // Load the home screen / login screen
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Navigator.fxml"));
    Parent root = loader.load();
    
    // Load the Main controller
    Navigator controller = loader.getController();
    // Give the Navigator access to the primary stage
    controller.setPrimaryStage(primaryStage);

    Scene scene = new Scene(root);
    // --- Set the minimum window dimensions ---
    primaryStage.setMinWidth(800);
    primaryStage.setMinHeight(600);
    // -----------------------------------------
    primaryStage.setScene(scene);
    primaryStage.show();
    if (Platform.isWindows()) {
      primaryStage.setTitle(WinAPI.TITLE_HASH);
      WinAPI.hideWindowTitleBar();
    }

    if (DEBUG) {
      Auth.login("test", "test123");
      controller.push(Route.HOME);
      return;
    }

    controller.push(Route.SPLASH);

    delay(7500, () -> {
      // Check if the user is logged in
      if (Auth.currentUser() == null) {
        controller.offAll(Route.LOGIN);
      } else {
        controller.push(Route.HOME);
      }
    });
  }

  public static void delay(long millis, Runnable continuation) {
    Task<Void> sleeper = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        try {
          Thread.sleep(millis);
        } catch (InterruptedException e) { }
        return null;
      }
    };
    sleeper.setOnSucceeded(event -> continuation.run());
    new Thread(sleeper).start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
