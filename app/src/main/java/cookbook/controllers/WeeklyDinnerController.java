package cookbook.controllers;

import cookbook.TestRecipe;
import cookbook.db.Recipe;
import cookbook.dishlist.MealTime;
import cookbook.dishlist.MenuManager;
import cookbook.dishlist.Weekday;
import cookbook.navigator.Route;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;

/**
 * Represents a week menu.
 */
public class WeeklyDinnerController extends RouteController implements Initializable {
  private MealTime[] dinnerTimeChoice = MealTime.values();
  private Recipe recipe;

  @FXML
  private ChoiceBox<MealTime> dinnerTime;

  @FXML
  private Button apply;

  @FXML
  private DatePicker pickDay;

  public void setData(Recipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    dinnerTime.getItems().addAll(dinnerTimeChoice);
  }

  @FXML
  private void handleApplyButtonClick() {
    MenuManager m = new MenuManager();
    try {
      m.loadMenus();
    } catch (Exception e) {
      System.err.println(e);
    }
    try {
      WeekFields weekFields = WeekFields.of(Locale.getDefault());
      m.addDish(new TestRecipe(recipe.getName(), recipe.getId()), dinnerTime.getValue(), getDate()
          .get(weekFields.weekOfWeekBasedYear()), getDate().getYear(), getWeekday());
      m.saveMenus();
      
      getNavigator().closeRoutePopup(Route.ADD_TO_WEEKLY_DINNER);
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  private Weekday getWeekday() {
    DayOfWeek dayOfWeek = getDate().getDayOfWeek();

    return switch (dayOfWeek) {
      case MONDAY -> Weekday.Monday;
      case TUESDAY -> Weekday.Tuesday;
      case WEDNESDAY -> Weekday.Wednesday;
      case THURSDAY -> Weekday.Thursday;
      case FRIDAY -> Weekday.Friday;
      case SATURDAY -> Weekday.Saturday;
      case SUNDAY -> Weekday.Sunday;
      default -> Weekday.Monday;
    };
  }

  private LocalDate getDate() {
    LocalDate date = pickDay.getValue();
    return date;
  }
}
