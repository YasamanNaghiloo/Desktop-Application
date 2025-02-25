package cookbook.controllers;

import java.util.ArrayList;
import java.util.ResourceBundle;

import cookbook.Admin;
import cookbook.db.User;
import cookbook.navigator.Route;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;
import java.net.URL;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Administration extends RouteController implements Initializable {
  ArrayList<User> users;
  ObservableList<User> userList = FXCollections.observableArrayList();
  public Stage mainWindow;

  public void setMainWindow(Stage mainWindow) {
    this.mainWindow = mainWindow;
  }

  @FXML
  private TableView<User> userTable;

  @FXML
  private TableColumn<User, Integer> userTable_ID;

  @FXML
  private TableColumn<User, Boolean> userTable_Administrator;

  @FXML
  private TableColumn<User, String> userTable_DisplayName;

  @FXML
  private TableColumn<User, String> userTable_UserName;

  @FXML
  void btAddUser(ActionEvent event) {
    getNavigator().push(Route.ADMIN_ADD_USER);
  }

  @FXML
  void btDeleteUser(ActionEvent event) {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser == null) {
      System.out.println("No user selected, skipping");
      return;
    }
    Admin.deleteUser(selectedUser.getUsername());
    load();
  }

  @FXML
  void btModifyUser(ActionEvent event) {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser == null) {
      System.out.println("No user selected, skipping");
      return;
    }
    ModifyUser modifyUser = (ModifyUser) getNavigator().push(Route.ADMIN_EDIT_USER);
    modifyUser.setUser(selectedUser);
    load();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    load();
  }

  public void load() {
    userTable.getItems().clear();
    loadCells();
    loadData();
  }

  public void loadCells() {
    userTable_UserName.setCellValueFactory(new PropertyValueFactory<>("username"));
    userTable_DisplayName.setCellValueFactory(new PropertyValueFactory<>("displayName"));
    userTable_Administrator.setCellValueFactory(new PropertyValueFactory<>("admin"));
    userTable_ID.setCellValueFactory(new PropertyValueFactory<>("id"));
  }

  public void loadData() {
    userList.removeAll(userList);
    ArrayList<User> currentUsers = Admin.getUsers();
    // users = new ArrayList<>();
    users = (ArrayList<User>) currentUsers.clone();
    userList.addAll(currentUsers);
    userTable.getItems().addAll(userList);
  }
}