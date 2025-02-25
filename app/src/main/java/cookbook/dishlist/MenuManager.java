package cookbook.dishlist;

import cookbook.RecipeInterface;
import cookbook.auth.Auth;
import cookbook.db.DB;
import cookbook.db.DatabaseStatement;
import cookbook.db.Recipe;
import cookbook.savelocal.FileEmptyer;
import cookbook.savelocal.ReadObjectsFromFile;
import cookbook.savelocal.SaveToFile;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the different menus.
 */
public class MenuManager implements Serializable {
  // Map where string is the year and week number as Year:Week
  private Map<WeekYearDate, WeekMenu> weekMenus;

  public MenuManager() {
    weekMenus = new HashMap<>();
  }

  /**
   * Adds a dish.
   *
   * @param recipe    The recipe to add.
   * @param timeOfDay The time of day the recipe should be cooked.
   * @param week      The week to add it to.
   * @param year      The year to add it to.
   * @param weekday   The weekday to add it to.
   */
  public void addDish(RecipeInterface recipe, MealTime timeOfDay, int week, int year,
      Weekday weekday) {
    WeekMenu weekMenu = getWeekMenu(week, year);
    if (weekMenu == null) {
      weekMenu = createWeekMenu(week, year);
    }
    try {
      weekMenu.addDish(new Dish(recipe, timeOfDay), weekday);
    } catch (IllegalArgumentException e) {
      System.err.println(e);
    }

  }

  protected WeekMenu getWeekMenu(int weekNumber, int year) {
    if (weekMenus.containsKey(new WeekYearDate(year, weekNumber))) {
      WeekMenu weekMenu = weekMenus.get(new WeekYearDate(year, weekNumber));
      return weekMenu;
    }
    return null;
  }

  /**
   * Returns copy of a week menu.
   *
   * @param weekNumber The number of the week.
   * @param year The year to get the week number from.
   * @return A copy of a week menu.
   */
  public WeekMenu getCopyOfWeekMenu(int weekNumber, int year) {
    if (weekMenus.containsKey(new WeekYearDate(year, weekNumber))) {
      WeekMenu weekMenu = (WeekMenu) weekMenus.get(new WeekYearDate(year, weekNumber)).copy();
      return weekMenu;
    }
    return null;
  }

  /**
   * Checks if a menu exists.
   *
   * @param weekNumber The number of the week to check.
   * @param year       The year of the menu you want to check.
   *
   * @return a boolean indicating if the menu exists or not.
   */
  public boolean weekMenuExists(int weekNumber, int year) {
    WeekMenu weekMenu = getWeekMenu(weekNumber, year);
    if (weekMenu == null) {
      return false;
    }
    return true;
  }

  private WeekMenu createWeekMenu(int weekNumber, int year) {
    WeekMenu weekMenu = new WeekMenu(year, weekNumber);
    weekMenus.put(getDate(year, weekNumber), weekMenu);
    return weekMenu;
  }

  /*
   * public ArrayList<String> menuToString(int year, int week) {
   * ArrayList<String> s = new ArrayList<>();
   * WeekMenu weekMenu = getWeekMenu(week, year);
   * s = weekMenu.getStringRepresentation();
   * return s;
   * }
   */

  private WeekYearDate getDate(int year, int weekNumber) {
    return new WeekYearDate(year, weekNumber);
  }

  /**
   * Gets all weekmenus in a year.
   *
   * @param year The year to get menus from.
   * @return The menus from the selected year.
   */
  public ArrayList<WeekMenu> getAllWeekMenusForYear(int year) {
    ArrayList<WeekMenu> menus = new ArrayList<>();
    for (Map.Entry<WeekYearDate, WeekMenu> menu : weekMenus.entrySet()) {
      if (menu.getKey().getYear() == year) {
        menus.add((WeekMenu) menu.getValue().copy());
      }
    }
    return menus;
  }

  // OBS OVERWRITES MENU WITH SAME YEAR AND WEEK
  public void addWeekMenu(WeekMenu menu) {
    weekMenus.put(getDate(menu.getYear(), menu.getWeekNumber()), menu);
  }

  /**
   * Saves the menus locally.
   *
   * @param path The path to save to.
   */
  public void saveMenus(String path) {
    FileEmptyer fileEmptyer = new FileEmptyer(path);
    try {
      fileEmptyer.emptyFile();
    } catch (Exception e) {
      System.err.println(e);
    }
    SaveToFile saver = new SaveToFile(path);
    ArrayList<WeekMenu> menus = new ArrayList<>();
    for (Map.Entry<WeekYearDate, WeekMenu> menu : weekMenus.entrySet()) {
      menus.add(menu.getValue());
    }
    ArrayList<Object> objectList = new ArrayList<>(menus);
    saver.writeObjectToFile(objectList);
  }

  /**
   * Saves the menus to database.
   */
  public void saveMenus() {
    System.out.println("Save");
    Calendar c = Calendar.getInstance();
    ArrayList<WeekMenu> menus = new ArrayList<>();
    DatabaseStatement databaseStatement = new DatabaseStatement();
    for (Map.Entry<WeekYearDate, WeekMenu> menu : weekMenus.entrySet()) {
      WeekMenu weekMenu = menu.getValue();
      int year = weekMenu.getYear();
      int weekNumber = weekMenu.getWeekNumber();

      for (int i = 0; i < Weekday.values().length; i++) {
        DayMenu dayMenu = weekMenu.getDayMenu(Weekday.values()[i]);
        LocalDate date = LocalDate.of(weekMenu.getYear(), 1, 1) // January 1st of the year
            .with(WeekFields.ISO.weekOfWeekBasedYear(), weekMenu.getWeekNumber())
            .with(WeekFields.ISO.dayOfWeek(), i + 1);
        ArrayList<Dish> dishs = dayMenu.getDishes();
        for (Dish dish : dishs) {
          databaseStatement.insert("menu",
            new String[] { "userId", "date", "mealTimeName", "recipeId" },
              new String[] { Integer.toString(Auth.currentUser().getId()), date.toString(),
                  dish.getTimeOfDay().toString(), Integer.toString(dish.getRecipe().getId()) },
              true);

        }

      }
      System.out.println("SAveend");
    }

  }

  /**
   * Loads menus that have been saved.
   *
   * @param path The path to laod from.
   */
  public void loadMenus(String path) {
    ReadObjectsFromFile<WeekMenu> reader = new ReadObjectsFromFile<WeekMenu>(path);
    ArrayList<WeekMenu> menus = new ArrayList<>();
    try {
      menus = reader.readObjectsfromFile();
    } catch (Exception e) {
      e.printStackTrace();
    }

    for (WeekMenu weekMenu : menus) {
      addWeekMenu(weekMenu);
    }

  }

  /**
   * Load the menus.
   */
  public void loadMenus() {
    System.out.println("LOAD");
    ArrayList<Integer> recipeIds = new ArrayList<>();
    ArrayList<Date> dates = new ArrayList<>();
    ArrayList<MealTime> mealTime = new ArrayList<>();
    try {
      PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(
          "SELECT recipe.recipeId, menu.date, menu.mealTimeName  FROM menu"
          + " LEFT JOIN recipe ON menu.recipeId = recipe.recipeId WHERE menu.userId = "
              + Auth.currentUser().getId());
      ResultSet result = stmt.executeQuery();
      while (result.next()) {
        recipeIds.add(result.getInt("recipeId"));
        dates.add(result.getDate("date"));
        mealTime.add(MealTime.valueOf(result.getString("mealTimeName")));
      }

    } catch (SQLException e) {
      System.err.println(e);
    }

    for (int i = 0; i < recipeIds.size(); i++) {
      Calendar c = Calendar.getInstance();
      c.setTime(dates.get(i));
      int day = c.get(Calendar.DAY_OF_WEEK);
      Weekday weekday;
      switch (day) {
        case 1:
          weekday = Weekday.Sunday;
          break;
        case 2:
          weekday = Weekday.Monday;
          break;
        case 3:
          weekday = Weekday.Tuesday;
          break;
        case 4:
          weekday = Weekday.Wednesday;
          break;
        case 5:
          weekday = Weekday.Thursday;
          break;
        case 6:
          weekday = Weekday.Friday;
          break;
        case 7:
          weekday = Weekday.Saturday;
          break;
        default:
          weekday = Weekday.Monday;
          break;
      }
      addDish(Recipe.fromDB(recipeIds.get(i)), mealTime.get(i),
          c.get(Calendar.WEEK_OF_YEAR),
          c.get(Calendar.YEAR), weekday);
      System.out.println("LOADend");
    }
  }
}
