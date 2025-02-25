package cookbook.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class myAnimation extends RouteController implements Initializable {
  private Stage mainWindow;

  @FXML
  private ImageView mascot;
  
  public void setMainWindow(Stage mainWindow) {
    this.mainWindow = mainWindow;
    //showMenu();
  }

  @Override
  public void initialize(URL arg0, ResourceBundle resources) {

    // Starting position (center)
    mascot.setTranslateX(90);
    mascot.setTranslateY(10);
    mascot.setScaleX(1);
    mascot.setScaleY(1);

    // Define animations 
    // Right (+X) movements + rotations
    TranslateTransition moveRight =
        new TranslateTransition(Duration.seconds(2), mascot);      // Move right
    moveRight.setToX(300);
    moveRight.setInterpolator(Interpolator.EASE_BOTH);
    moveRight.setAutoReverse(true);

    RotateTransition rotateRight =
        new RotateTransition(Duration.seconds(2), mascot);          // Rotate right
    rotateRight.setByAngle(360);

    // Left (-X) movements + rotations
    TranslateTransition moveLeft =
        new TranslateTransition(Duration.seconds(2), mascot);       // Move left
    moveLeft.setToX(-100);
    moveLeft.setInterpolator(Interpolator.EASE_BOTH);
    moveLeft.setAutoReverse(true);

    RotateTransition rotateLeft =
        new RotateTransition(Duration.seconds(2), mascot);   // Rotate left
    rotateLeft.setByAngle(-360);

    // Return to origional pos to prep for expansion
    TranslateTransition returnOriginalPosX = new TranslateTransition(Duration.seconds(2), mascot);
    returnOriginalPosX.setToX(180);
    RotateTransition returnOriginalPos = new RotateTransition(Duration.seconds(2), mascot);
    returnOriginalPos.setByAngle(360);

    // Parallel movments cuz haha funne
    ParallelTransition moveLeftAndRotate = 
        new ParallelTransition(moveLeft, rotateLeft);     // Combine move + rotate for left side
    ParallelTransition moveRightAndRotate = 
        new ParallelTransition(moveRight, rotateRight);     // Combine move + rotate for right side
    ParallelTransition returnToOrigionalPosition =
        new ParallelTransition(returnOriginalPosX, returnOriginalPos); //image to origional position

    // EXPAND !!!!
    ScaleTransition expand = new ScaleTransition(Duration.seconds(1), mascot);       // >__<
    expand.setToX(20);
    expand.setToY(20);
    expand.setInterpolator(Interpolator.EASE_OUT);

    // Sequential animation
    SequentialTransition seqTransition = new SequentialTransition(
        new PauseTransition(Duration.seconds(1)), // Delay before starting
        moveRightAndRotate,
        moveLeftAndRotate,
        returnToOrigionalPosition,
          expand
        );

    // Play the sequential animation
    seqTransition.play();
  }
}