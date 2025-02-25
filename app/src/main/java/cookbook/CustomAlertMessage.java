package cookbook;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


/** This class is made just in sake of simplicity, in order to handle.
 * errors easier.
 * 
 */
public class CustomAlertMessage {

  private String contentMessage;

  public CustomAlertMessage() {

  }

  /** Is the method in order to create an alert message.

   * @param x is the exception.getmessage().
   * @return returns
   */
  public Alert createCustomAlert(String x) {
    this.contentMessage = x;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Error!!!");
    alert.setContentText(contentMessage);

    // Create a Label with the content message and set the font
    Label label = new Label(contentMessage);
    label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    label.setTextFill(Color.BLACK);
    alert.getDialogPane().setContent(label);

    return alert;
  }
}
