package cookbook.dishlist;

import java.net.URL;
import java.time.chrono.Chronology;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

public class WeeklyDinnerController implements Initializable {
  private Stage mainWindow;
  private String[] dinnerTimeChoice = {"Breakfast", "Lunch", "Dinner"};
  private Chronology date;

  @FXML
  private ChoiceBox<String> dinnerTime;

  @FXML
  private Button apply;

  @FXML
  private DatePicker pickDay;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    dinnerTime.getItems().addAll(dinnerTimeChoice);
  }

  public void setMainWindow(Stage mainWindow) {
    this.mainWindow = mainWindow;
    //showMenu();
  }
}

