package cookbook.controllers;

import java.sql.SQLException;

import cookbook.Admin;
import cookbook.navigator.Route;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AddUser extends RouteController {

  @FXML
  private TextField txDisplayname;

  @FXML
  private PasswordField txPassword;

  @FXML
  private TextField txUsername;

  @FXML
  void btRegisterUser(ActionEvent event) throws SQLException {
    System.out.println(txUsername.getText());
    System.out.println(txDisplayname.getText());
    System.out.println(txPassword.getText());
    
    Admin.addUser(this.txUsername.getText(), this.txDisplayname.getText(), this.txPassword.getText());
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


  // protected String getDisplayNameInfo(){
  //   return txDisplayname.get;
  //}

  // public String getTxUserName(){
  //   return txUsername.getText();
  // }

  // public String getTxDisplayName(){
  //   return txDisplayname.getText();
  // }

  // public String getTxPassword(){
  //   return txPassword.getText();
  // }

}
