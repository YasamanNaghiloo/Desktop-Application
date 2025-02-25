package cookbook.controllers;

import java.sql.SQLException;

import cookbook.Admin;
import cookbook.db.User;
import cookbook.navigator.Route;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.*;

public class ModifyUser extends RouteController { 

  @FXML
  private TextField txDisplayname;

  @FXML
  private TextField txUsername;

  private Stage mainWindow;
  private User user;

  public void setUser(User user) {
    this.user = user;
    System.out.println(user);
    this.txUsername.setText(user.getUsername());
    this.txDisplayname.setText(user.getDisplayName());
  }

  public void setMainWindow(Stage mainWindow) {
    this.mainWindow = mainWindow;
    //this.user = user;
  }

  /*
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    loadData();
  }

  public void loadData() {
    System.out.println(user.getDisplayName());
    //this.txUsername.setText(user.getDisplayName());
  }
  */

  @FXML
  void btModifyUser(ActionEvent event) throws SQLException {
    //String title = txDisplayname.getText();
    //mainWindow.setTitle(title);
    Admin.modifyUser(user.getId(), txUsername.getText(), txDisplayname.getText());
    try {
      Administration adminController = (Administration) getNavigator().getHistoryItem(Route.ADMIN).getController();
      adminController.load();
    } catch (Exception e) {
      System.out.println("Error finding admin controller, going back to home screen: " + e);
      getNavigator().goBack();
    } finally {
      getNavigator().goBack();
    }
  }

}
