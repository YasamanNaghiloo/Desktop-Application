package cookbook.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import cookbook.auth.Auth;
import cookbook.navigator.Route;

public class LoginController extends RouteController {
  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  /**
   * Never ever use this constructor, it's only for JavaFX
   */
  public LoginController() {}

  public void btHandleLogin() {
    switch (Auth.login(usernameField.getText(), passwordField.getText())) {
      case SUCCESS -> {
        usernameField.clear();
        passwordField.clear();
        getNavigator().offAll(Route.HOME);
      }
      case INVALID_USERNAME ->
        getNavigator().showDialog("Error", "Invalid username.");
      case INVALID_PASSWORD ->
        getNavigator().showDialog("Error", "Invalid password.");
      case SQL_ERROR ->
        getNavigator().showDialog("Error", "An error has occurred with the database.");
      case UNKNOWN_ERROR ->
        getNavigator().showDialog("Error", "An unknown error has occurred.");
    }
  }
}
