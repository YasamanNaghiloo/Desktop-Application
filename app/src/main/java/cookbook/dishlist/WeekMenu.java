package cookbook.dishlist;

import cookbook.Copyable;
import cookbook.RecipeInterface;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The menu for a week.
 */
public class WeekMenu implements Serializable, Copyable {
  private Map<Weekday, DayMenu> dayMenus;
  private int year;
  private int weekNumber;

  /**
   * Create week menu.
   *
   * @param year The year of the week menu.
   * @param weekNumber The weeknumber.
   */
  public WeekMenu(int year, int weekNumber) {
    dayMenus = new HashMap<>();
    setWeeknumber(weekNumber);
    setYear(year);
  }

  public WeekMenu(WeekMenu other) {
    this.year = other.year;
    this.weekNumber = other.weekNumber;
    this.dayMenus = new HashMap<>();
    for (Map.Entry<Weekday, DayMenu> entry : other.dayMenus.entrySet()) {
      this.dayMenus.put(entry.getKey(), new DayMenu(entry.getValue()));
    }
}

  private void setYear(int year) {
    this.year = year;
  }

  private void setWeeknumber(int weekNumber) {
    if (weekNumber > 53 || weekNumber < 1) {
      throw new IllegalArgumentException("Weeknumber has to be in the range 1 to 53.");
    }
    this.weekNumber = weekNumber;
  }

  public int getWeekNumber() {
    return weekNumber;
  }

  public int getYear() {
    return year;
  }

  protected void addDish(Dish dish, Weekday weekday) {
    getDayMenu(weekday).addDish(dish);
  }

  protected DayMenu getDayMenu(Weekday weekday) {
    DayMenu dayMenu = checkDayMenu(weekday);
    if (dayMenu == null) {
      dayMenu = createDayMenu(weekday);
    }
    return dayMenu;
  }

  private DayMenu checkDayMenu(Weekday weekDay) {
    return dayMenus.get(weekDay);
  }

  private DayMenu createDayMenu(Weekday weekday) {
    DayMenu dayMenu = new DayMenu(weekday);
    dayMenus.put(weekday, dayMenu);
    return dayMenu;
  }

  public ArrayList<ArrayList<ArrayList<String>>> getStringRepresentation() {
    ArrayList<ArrayList<ArrayList<String>>> menuRows = new ArrayList<>();
    for (int i = 0; i < Weekday.values().length; i++) {
      menuRows.add(new ArrayList<ArrayList<String>>());
    }
    DayMenu[] days = getDayMenus();
    Arrays.sort(days);
    
    Weekday[] weekdays = Weekday.values();
      
    for (DayMenu menu : days) {
      for (int i = 0; i < weekdays.length; i++) {
        if (weekdays[i] == menu.getWeekDay()) {
          ArrayList<ArrayList<String>> rows = menu.getStringRepresentation();
          menuRows.set(i, rows);
          //menuRows.add(weekday.toString());
        }
        
        //menuRows.addAll(menu.getStringRepresentation());
        
      }
    }
    return menuRows;
  }

  public ArrayList<ArrayList<ArrayList<RecipeInterface>>> getRecipeRepresentation() {
    ArrayList<ArrayList<ArrayList<RecipeInterface>>> menuRows = new ArrayList<>();
    for (int i = 0; i < Weekday.values().length; i++) {
      menuRows.add(new ArrayList<ArrayList<RecipeInterface>>());
    }
    DayMenu[] days = getDayMenus();
    Arrays.sort(days);
    
    Weekday[] weekdays = Weekday.values();
      
    for (DayMenu menu : days) {
      for (int i = 0; i < weekdays.length; i++) {
        if (weekdays[i] == menu.getWeekDay()) {
          ArrayList<ArrayList<RecipeInterface>> rows = menu.getRecipeRepresentation();
          menuRows.set(i, rows);
          //menuRows.add(weekday.toString());
        }
        
        //menuRows.addAll(menu.getStringRepresentation());
        
      }
    }
    return menuRows;
  }

  private DayMenu[] getDayMenus() {
    Collection<DayMenu> dayCollection =  this.dayMenus.values();
    ArrayList<DayMenu> menus = new ArrayList<>();

    try {
      menus = new ArrayList<>(dayCollection);
    } catch (Exception e) {
      System.out.println(e);
    }
    DayMenu[] dayMenus = new DayMenu[menus.size()];
    for (int i = 0; i < menus.size(); i++) {
      dayMenus[i] = new DayMenu(menus.get(i).getWeekDay(), menus.get(i).getDishes());
    }
   
    return dayMenus;
  }

  @Override
  public String toString() {
    return "Week: " + getWeekNumber();
  } 

  @Override
  public Copyable copy() {
    return new WeekMenu(this);
  }

  public ArrayList<RecipeInterface> getRecipes() {
    ArrayList<RecipeInterface> recipes = new ArrayList<>();
    DayMenu[] days = getDayMenus();
    for (DayMenu dayMenu : days) {
      ArrayList<Dish> dishes= dayMenu.getDishes();
      for (Dish dish : dishes) {
        recipes.add(dish.getRecipe());
      }
    }
    return recipes;
  }
}
